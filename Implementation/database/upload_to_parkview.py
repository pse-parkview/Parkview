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

blas_data_dirs = ['Xeon_Gold_6230/omp/blas.json', 'V100_SXM2/cuda/blas.json', 'MI100/hip/blas.json']
matrix_data_dirs = ['Xeon_Gold_6230-solver/omp/SuiteSparse', 'Xeon_Gold_6230/omp/SuiteSparse', 'V100_SXM2-solver/cuda/SuiteSparse', 'V100_SXM2/cuda/SuiteSparse', 'MI100/hip/SuiteSparse/', 'MI100-solver/hip/SuiteSparse']


for blas_file in blas_data_dirs:
    blas_path = os.path.join(args.data, blas_file)
    try:
        with open(blas_path) as f:
            blas_data = json.load(f)
        blas_params = {'sha': args.sha, 'device': blas_file.split('/')[0], 'blas': True}
        requests.post(url = PARKVIEW_ENDPOINT, json = blas_data, params = blas_params)
        print(f'{blas_path} is fine')
    except KeyboardInterrupt:
        exit()
    except:
        print(f'### ERROR: {blas_path} is broken')

for matrix_file in matrix_data_dirs:
    matrix_path = os.path.join(args.data, matrix_file)
    data = []
    for matrix_group in os.listdir(matrix_path):
        matrix_group_path = os.path.join(matrix_path, matrix_group)

        for matrix_runs in os.listdir(matrix_group_path):
            matrix_runs_path = os.path.join(matrix_group_path, matrix_runs)
            try:
                with open(matrix_runs_path) as f:
                    data.append(*json.load(f))
            except KeyboardInterrupt:
                exit()
            except:
                print(f'### ERROR: {matrix_runs_path} is broken')

    params = {'sha': args.sha, 'device': matrix_file.split('/')[0]}
    requests.post(url = PARKVIEW_ENDPOINT, json = data, params = params)
    print(f'{matrix_path} is fine')
            
