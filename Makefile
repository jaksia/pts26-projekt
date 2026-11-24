check_and_test: FORCE
	mvn test

lint: FORCE
	mvn checkstyle:check

format: FORCE
	mvn formatter:format

validate: FORCE
	mvn formatter:validate
FORCE: ;
