import requests
import json

class ParkviewApiDriver:
    def __init__(self, parkview_url: str):
        self.parkview_url = parkview_url

    def upload_file(self, path: str, sha: str, device: str):
        params = {'sha': sha, 'device': device}
        endpoint = self.parkview_url + '/post'
        print('loading file ... ', end='')
        with open(path, 'r') as f:
            data = json.load(f)
        print('done')

        print('uploading ... ', end='')
        requests.post(url=endpoint, json=data, params=params)
        print('done')

