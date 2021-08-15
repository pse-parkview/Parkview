#!/usr/bin/env python3
from test_cases.t1 import T1
from test_utils.parkview_webdriver import ParkviewWebDriver
from test_utils.parkview_apidriver import ParkviewApiDriver

parkview_url = 'http://localhost:4200'
backend_url = 'http://localhost:8080/parkview'

available_commit = 'f8493edccbba242b737403d4d21688221b379b56'
available_device = 'MI100'

data_file = 'resources/test_single_conversion.json'

api_driver = ParkviewApiDriver(backend_url)
web_driver = ParkviewWebDriver(parkview_url)

testcases = [T1(web_driver, available_commit, available_device)]

## setup
api_driver.upload_file(data_file, available_commit, available_device)
web_driver.init()

if __name__ == '__main__':
    for testcase in testcases:
        testcase.run()
