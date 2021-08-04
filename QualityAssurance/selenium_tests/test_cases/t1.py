from test_cases.test_case import TestCase
from test_utils.parview_webdriver import ParkviewWebDriver


class T1(TestCase):
    def __init__(self, driver: ParkviewWebDriver):
        super().__init__(driver)

    def run(self):
        self.driver.accept_cookies()
        self.driver.toggle_sidebar()
        self.driver.select_benchmark('Conversion')
        self.driver.select_branch('develop')
        self.driver.select_datapoint('404a31', 'MI100')
        self.driver.toggle_sidebar()
        self.driver.open_configuration_dialog()
        self.driver.confirm_plot()
