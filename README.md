
[![CI](https://github.com/pse-parkview/PSE_dashboard/actions/workflows/ci.yml/badge.svg?branch=develop)](https://github.com/pse-parkview/PSE_dashboard/actions/workflows/ci.yml) ![Develop Test Coverage Frontend](https://gist.githubusercontent.com/tadachs/2f350a3c58fed9515b658495edc70191/raw/parkview_develop_frontend_coverage.svg) ![LOC Frontend](https://gist.githubusercontent.com/tadachs/2f350a3c58fed9515b658495edc70191/raw/parkview_develop_frontend_loc.svg)

# Parkview
*Performance Dashboard for Continuous Benchmarking of HPC Libraries*

Parkview gives it users a new way to interact with their performance data. By linking it to the Git history of a given project, developers achieve deeper insights into their codes performance than ever before. 

Parkview runs completly in your browser, reading its data directly from a GitHub Repository

## Want to have look? 
Take a look at our [github.io page](https://pse-parkview.github.io/Parkview/).

## Want to run it yourself?
Just clone the repo and run `npm i && npm run ng serve`

## Miscellaneous
The logic for processing the data is installable with npm, see [parkview-lib](https://github.com/pse-parkview/parkview-lib) for more information.
The benchmark and git data is currently stored in [parkview-data](https://github.com/pse-parkview/parkview-data).
