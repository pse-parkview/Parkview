package com.parkview.parkview.processing.transforms

class InvalidPlotTransformException(s: String) : Exception(s)

class InvalidPlotOptionsException(options: Map<String, String>, option: String) : Exception(
    "Error, ${options[option]} is not a valid value for $option"
)

