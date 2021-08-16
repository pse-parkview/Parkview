from test_cases.test_case import TestCase
from test_utils.parkview_webdriver import ParkviewWebDriver
from test_utils.parkview_apidriver import ParkviewApiDriver

import clipboard


class T5(TestCase):
    name = "T5"

    def __init__(self, web_driver: ParkviewWebDriver, api_driver: ParkviewApiDriver, available_commit: str, available_device: str, benchmark_file: str):
        super().__init__(web_driver, api_driver)
        self.available_commit = available_commit
        self.available_device = available_device
        self.benchmark_file = benchmark_file

    def run(self):
        self.web_driver.accept_cookies()
        self.web_driver.toggle_sidebar()
        self.web_driver.select_benchmark('Conversion')
        self.web_driver.select_branch('develop')
        self.web_driver.select_datapoint(self.available_commit, self.available_device)
        self.web_driver.toggle_sidebar()
        self.web_driver.open_configuration_dialog()
        self.web_driver.confirm_plot()
        self.web_driver.toggle_sidebar()
        self.web_driver.click_share_button()
        if clipboard.paste() != self.web_driver.get_current_url():
            raise Exception(f'Error, clipboard text doesn\'t match url {clipboard.paste} != {self.web_driver.get_current_url()}')

    def setup(self):
        self.api_driver.upload_file(self.benchmark_file, self.available_commit, self.available_device)

