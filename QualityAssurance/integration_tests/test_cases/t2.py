from test_cases.test_case import TestCase
from test_utils.parkview_webdriver import ParkviewWebDriver
from test_utils.parkview_apidriver import ParkviewApiDriver


class T2(TestCase):
    name = "T2"

    def __init__(self, web_driver: ParkviewWebDriver, api_driver: ParkviewApiDriver, available_commit: str, available_device: str, benchmark_file: str):
        super().__init__(web_driver, api_driver)
        self.available_commit = available_commit
        self.available_device = available_device
        self.benchmark_file = benchmark_file

    def setup(self):
        self.api_driver.upload_file(self.benchmark_file, self.available_commit, self.available_device)

    def run(self):
        self.web_driver.accept_cookies()
        self.web_driver.toggle_sidebar()
        self.web_driver.select_benchmark('Conversion')
        self.web_driver.select_branch('develop')
        
        if not self.web_driver.assert_commit_available(self.available_commit, self.available_device):
            raise Exception()

