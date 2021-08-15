#!/usr/bin/env python3
from test_cases.t1 import T1
from test_utils.parkview_apidriver import ParkviewApiDriver
from test_utils.parkview_webdriver import ParkviewWebDriver
from test_utils.docker_driver import DockerDriver

import argparse
import time
import traceback

parser = argparse.ArgumentParser(description='Runs integration tests for parkview')
parser.add_argument('--backend', type=str, required=True, help='Backend address')
parser.add_argument('--frontend', type=str, required=True, help='frontend address')
parser.add_argument('--selenium', type=str, default='', help='selenium address')

parser.add_argument('--backend-img', type=str, default='../../Implementation/backend', help='path to backend dockerfile')
parser.add_argument('--frontend-img', type=str, default='../../Implementation/frontend', help='path to frontend dockerfile')

parser.add_argument('--github-token', type=str, default='', help='github oauth token')
parser.add_argument('--github-user', type=str, default='', help='github oauth username')

args = parser.parse_args()

parkview_url = args.frontend
backend_url = args.backend
selenium_url = args.selenium

available_commit = 'f8493edccbba242b737403d4d21688221b379b56'
available_device = 'MI100'

data_file = 'resources/test_single_conversion.json'

api_driver = ParkviewApiDriver(backend_url)
web_driver = ParkviewWebDriver(parkview_url, selenium_url=selenium_url)
web_driver.init()

testcases = [T1(web_driver, api_driver, available_commit, available_device, data_file)]

docker_driver = DockerDriver(args.backend_img, args.frontend_img, token=args.github_token, user=args.github_user)


# setup
if __name__ == '__main__':
    try:

        for testcase in testcases:
            docker_driver.start_backend()
            docker_driver.start_frontend()
            print('started containers')
            time.sleep(30)

            print('init test case')

            testcase.setup()
            web_driver.reload_site()

            print('running testcase')
            testcase.run()
            print(f'test case {testcase.name} finished sucessfully')
    except Exception as e:
        print(f'Failed. Error: ')
        traceback.print_exc()
        docker_driver.stop_frontend()
        docker_driver.stop_backend()
        exit(1)

    docker_driver.stop_frontend()
    docker_driver.stop_backend()
