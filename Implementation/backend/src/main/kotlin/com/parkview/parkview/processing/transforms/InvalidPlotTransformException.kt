package com.parkview.parkview.processing.transforms

class InvalidPlotTransformException(s: String) : Exception(s)

class InvalidPlotOptionValueException(options: Map<String, String>, option: String) : Exception(
    "Error, ${options[option]} is not a valid value for $option"
)

class InvalidPlotOptionNameException(name: String) : Exception(
    "Error, $name is not a valid option"
)
