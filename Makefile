development:
	docker-compose \
		--project-directory=${PWD} \
		--project-name=footballbot \
		-f deploy/docker-compose.development.yml \
		up -d

stop-development:
	docker-compose \
		--project-directory=${PWD} \
		--project-name=footballbot \
		-f deploy/docker-compose.development.yml \
		down

psql:
	PGPASSWORD=01ff11e3a0ff2e756b6e949f1a3d76ec4fb852decbd9f2467c56ea220298925c \
	psql --host=ec2-63-34-153-52.eu-west-1.compute.amazonaws.com --username=rimlcxkyldvdrj --dbname=d13q0bm88erbcf