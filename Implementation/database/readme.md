### setup

- start docker container (`docker-compose up`)
- compile and start backend (commented out in docker-compose since I haven't found the time to add configuration to the backend yet)
- clone ginkgo-data repo and run
```
python3 upload_to_parkview.py --sha [some sha] --data [path to ginkgo-data/data]
```

That should upload all the data from the repo to the database. For `[some sha]` use some sha from ginko repo (not gingko-data), for example `a9e3c79a71a3179c58976a326d0d8266ee589868`
You can also checkout the `common_kernes` branch in the ginkgo data repository and upload the data for a different commit so you have something that can be compared.
