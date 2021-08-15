import docker


class DockerDriver:
    backend_container = None
    frontend_container = None

    def __init__(self, backend_path: str, frontend_path: str, user: str = '', token: str = ''):
        self.client = docker.from_env()
        self.client.images.build(path=backend_path, tag='parkview-backend-test')
        self.client.images.build(path=frontend_path, tag='parkview-frontend-test')
        self.user = user
        self.token = token

    def start_backend(self):
        self.stop_backend()
        print('starting backend')
        self.backend_container = self.client.containers.run(image='parkview-backend-test', detach=True,
                command=f'--parkview.database.embedded=true --parkview.git-api.owner=pse-parkview --parkview.git-api.repoName=ginkgo --parkview.git-api.firstCommitSha=3eca4d1c25cb04a084dd77c6e8c273da9603e17f --parkview.git-api.username={self.user} --parkview.git-api.token={self.token}',
                ports={'8080':'8080'},
                auto_remove=True,
                remove=True,
                )
        self.start_frontend()

    def start_frontend(self):
        self.stop_frontend()
        print('starting frontend')
        self.frontend_container = self.client.containers.run(image='parkview-frontend-test', detach=True,
                command=f'--proxy-config /app/src/proxy-container.conf.json --host 0.0.0.0 --port 4200',
                ports={'4200':'4200'},
                auto_remove=True,
                remove=True,
                links={self.backend_container.name: 'parkview-backend'},
                )

    def stop_backend(self):
        if self.backend_container is not None:
            print('stopping backend')
            self.backend_container.kill()

    def stop_frontend(self):
        if self.frontend_container is not None:
            print('stopping frontend')
            self.frontend_container.kill()

