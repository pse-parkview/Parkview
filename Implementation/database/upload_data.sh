#!/bin/sh

GINKGO_DATA_PATH=$1

COMMIT_A=47ba37da636cb5b5d4142eaa196bcd5218357055
COMMIT_B=d17511655c09acfec159ce55e0fb919f5d8747d5

checkout_and_upload() {
	cd $3
	git checkout $2
	cd - 
	python3 upload_to_parkview.py --sha $1 --data $GINKGO_DATA_PATH
}


checkout_and_upload $COMMIT_A master $GINKGO_DATA_PATH
checkout_and_upload $COMMIT_B common_kernels $GINKGO_DATA_PATH
