import docker


class DockerDriver:
    backend_container = None
    frontend_container = None

    def __init__(self, backend_container: str, frontend_container: str, user: str = '', token: str = ''):
        self.client = docker.from_env()
        self.backend_container_image = backend_container
        self.frontend_container_image = frontend_container
        self.user = user
        self.token = token

    def start_backend(self):
        self.stop_backend()
        print(f'starting {self.backend_container_image}')
        self.backend_container = self.client.containers.run(image=self.backend_container_image, detach=True,
                command=f'--parkview.database.embedded=true --parkview.git-api.owner=pse-parkview --parkview.git-api.repoName=ginkgo --parkview.git-api.firstCommitSha=3eca4d1c25cb04a084dd77c6e8c273da9603e17f --parkview.git-api.username={self.user} --parkview.git-api.token={self.token}',
                ports={'8080':'8080'},
                auto_remove=True,
                remove=True,
                )
        self.start_frontend()

    def start_frontend(self):
        self.stop_frontend()
        print(f'starting {self.frontend_container_image}')
        self.frontend_container = self.client.containers.run(image=self.frontend_container_image, detach=True,
                ports={'4200':'4200'},
                auto_remove=True,
                remove=True,
                links={self.backend_container.name: 'parkview-backend'},
                )

    def stop_backend(self):
        if self.backend_container is not None:
            print(f'stopping {self.backend_container_image}')
            self.backend_container.kill()

    def stop_frontend(self):
        if self.frontend_container is not None:
            print(f'stopping {self.frontend_container_image}')
            self.frontend_container.kill()

