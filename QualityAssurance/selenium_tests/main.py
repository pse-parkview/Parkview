#!/usr/bin/env python3
from test_cases.t1 import T1
from test_utils.parview_webdriver import ParkviewWebDriver

parkview_url = 'http://localhost:4200'

driver = ParkviewWebDriver(parkview_url)

testcases = [T1(driver)]

if __name__ == '__main__':
    for testcase in testcases:
        testcase.run()
