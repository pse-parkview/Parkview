#!/bin/sh

GINKGO_DATA_PATH=$1

COMMIT_A=26ee87ee6ecc3af1b7b455b81f5733530c31424d
COMMIT_B=0d7a0e9e72cecac270b507f5f7cf806ca7183d3a

checkout_and_upload() {
	cd $3
	git checkout $2
	cd - 
	python3 upload_to_parkview.py --sha $1 --data $GINKGO_DATA_PATH
}


checkout_and_upload $COMMIT_A master $GINKGO_DATA_PATH
checkout_and_upload $COMMIT_B common_kernels $GINKGO_DATA_PATH
