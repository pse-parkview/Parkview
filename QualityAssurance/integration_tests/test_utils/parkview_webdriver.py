from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions
import time


class ParkviewWebDriver:
    def __init__(self, parkview_url: str, selenium_url: str = ''):
        self.url = parkview_url
        # self.options.add_argument('ignore-certificate-errors')
        # self.options.add_argument('headless')

    def init(self):
        self.options = webdriver.ChromeOptions()
        self.driver = webdriver.Chrome(options=self.options)

        self.driver.get(self.url)


    def wait_and_click(self, by: By, value: str):
        WebDriverWait(self.driver, 10).until(
            expected_conditions.element_to_be_clickable(
                (by, value)
            )
        ).click()
        time.sleep(0.1)

    def select_branch(self, branch_name: str):
        self.wait_and_click(By.ID, 'branchSelection')
        self.wait_and_click(By.XPATH, f'//mat-option/span[contains(text(), " {branch_name} ") and contains('
                                      f'@class, "mat-option-text")]')
        # self.wait_and_click(f'//span[contains(text(), " {branch_name} ") and contains(@class, "mat-option-text")]')

    def select_benchmark(self, benchmark_name: str):
        self.wait_and_click(By.ID, 'benchmarkSelection')
        self.wait_and_click(By.XPATH, f'//mat-option/span[contains(text(), " {benchmark_name} ") and contains('
                                      f'@class, "mat-option-text")]')

    def select_datapoint(self, sha: str, device: str):
        commit_panel_path = f'//mat-expansion-panel[contains(@class, commitPanel)]/mat-expansion-panel-header/span/span[' \
                            f'contains(text(), "{sha[:6]}")]/../../..'
        self.wait_and_click(By.XPATH, commit_panel_path)
        self.wait_and_click(By.XPATH,
                            f'{commit_panel_path}/div/div/section/ul/li/mat-checkbox/label/span[contains(text(), " {device} ")]')
        self.wait_and_click(By.XPATH, f'{commit_panel_path}/div/mat-action-row/button/span')
        self.wait_and_click(By.XPATH, commit_panel_path)

    def open_configuration(self):
        self.wait_and_click('//*[@id="sidenav"]/div/app-sidebar/app-side-current-chosen-commit/mat-card/button')

    def select_device(self, device_name: str):
        self.wait_and_click(f'//mat-checkbox/label/span[contains(text(), " {device_name} ")]')

    def accept_cookies(self):
        self.wait_and_click(By.ID, 'confirmCookies')

    def toggle_sidebar(self):
        self.wait_and_click(By.ID, 'sidebarToggleButton')

    def open_configuration_dialog(self):
        self.wait_and_click(By.ID, 'configurePlotButton')

    def confirm_plot(self):
        self.wait_and_click(By.ID, 'plotButton')
