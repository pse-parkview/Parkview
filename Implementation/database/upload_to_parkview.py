#!/usr/bin/env python3
import argparse
import os
import requests
import json

PARKVIEW_ENDPOINT = 'http://localhost:8080/post'

parser = argparse.ArgumentParser(description='Process some integers.')
parser.add_argument('--sha', type=str, required=True, help='sha of commit')
parser.add_argument('--data', type=str, required=True, help='path to ginko-data/data')

args = parser.parse_args()

for device_dir in os.listdir(args.data):
    device_path = os.path.join(args.data, device_dir)
    if not os.path.isdir(device_path):
        continue

    for lib_dir in os.listdir(device_path):
        blas_path = os.path.join(device_path, lib_dir, 'blas.json')
        device_name = device_dir + '_' + lib_dir

        if os.path.isfile(blas_path):
            print(blas_path)
            try:
                with open(blas_path) as f:
                    blas_data = json.load(f)
                print(f'{blas_path} is fine')
                blas_params = {'sha': args.sha, 'device': device_name, 'blas': True}
                requests.post(url = PARKVIEW_ENDPOINT, json = blas_data, params = blas_params)
            except KeyboardInterrupt:
                exit()
            except:
                print(f'### ERROR: {blas_path} is fucked')

        lib_path = os.path.join(device_path, lib_dir, 'SuiteSparse')

        data = []

    
        for benchmark_name in os.listdir(lib_path):
            benchmark_path = os.path.join(lib_path, benchmark_name)

            for datapoint in os.listdir(benchmark_path):
                datapoint_path = os.path.join(benchmark_path, datapoint)

                try:
                    with open(datapoint_path) as f:
                        data.append(*json.load(f))
                    print(f'{datapoint_path} is fine')
                except KeyboardInterrupt:
                    exit()
                except:
                    print(f'### ERROR: {datapoint_path} is fucked')

        params = {'sha': args.sha, 'device': device_name}
        requests.post(url = PARKVIEW_ENDPOINT, json = data, params = params)

