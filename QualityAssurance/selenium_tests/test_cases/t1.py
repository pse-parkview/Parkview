from test_cases.test_case import TestCase
from test_utils.parkview_webdriver import ParkviewWebDriver


class T1(TestCase):
    def __init__(self, driver: ParkviewWebDriver, available_commit: str, available_device: str):
        super().__init__(driver)
        self.available_commit = available_commit
        self.available_device = available_device

    def run(self):
        self.driver.accept_cookies()
        self.driver.toggle_sidebar()
        self.driver.select_benchmark('Conversion')
        self.driver.select_branch('develop')
        self.driver.select_datapoint(self.available_commit, self.available_device)
        self.driver.toggle_sidebar()
        self.driver.open_configuration_dialog()
        self.driver.confirm_plot()
        self.driver.toggle_sidebar()
