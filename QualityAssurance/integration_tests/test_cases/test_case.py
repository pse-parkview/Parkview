from abc import abstractmethod, ABC

from test_utils.parkview_webdriver import ParkviewWebDriver


class TestCase(ABC):
    def __init__(self, driver: ParkviewWebDriver):
        self.driver = driver

    @abstractmethod
    def run(self):
        raise NotImplementedError()
