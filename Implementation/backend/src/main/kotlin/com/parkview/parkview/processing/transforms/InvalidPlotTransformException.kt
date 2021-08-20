package com.parkview.parkview.processing.transforms

class InvalidPlotTransformException(s: String) : Exception(s)

class InvalidPlotConfigValueException(value: String?, option: String) : Exception(
    "Error, $value is not a valid value for $option"
)

class InvalidPlotConfigNameException(name: String) : Exception(
    "Error, $name is not a valid option"
)
