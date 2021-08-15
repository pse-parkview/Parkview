from abc import abstractmethod, ABC

from test_utils.parkview_webdriver import ParkviewWebDriver
from test_utils.parkview_apidriver import ParkviewApiDriver


class TestCase(ABC):
    def __init__(self, web_driver: ParkviewWebDriver, api_driver: ParkviewApiDriver):
        self.web_driver = web_driver
        self.api_driver = api_driver

    @abstractmethod
    def setup(self):
        raise NotImplementedError()

    @abstractmethod
    def run(self):
        raise NotImplementedError()

    @property
    @abstractmethod
    def name(self):
        raise NotImplementedError()

