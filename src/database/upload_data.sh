#!/bin/sh

GINKGO_DATA_PATH=$1

COMMIT=$(curl https://api.github.com/repos/pse-parkview/ginkgo/commits | head | grep sha | grep '"[^"]*",' -o | sed 's/"\|,//g')

checkout_and_upload() {
	cd $3
	git checkout $2
	cd - 
	python3 upload_to_parkview.py --sha $1 --data $GINKGO_DATA_PATH
}


checkout_and_upload $COMMIT master $GINKGO_DATA_PATH
#checkout_and_upload $COMMIT_A master $GINKGO_DATA_PATH
#checkout_and_upload $COMMIT_B common_kernels $GINKGO_DATA_PATH
