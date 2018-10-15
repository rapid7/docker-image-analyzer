## Docker Image Analyzer

Extracts, parses, and analyzes [Docker](https://www.docker.com) images into Java objects with JSON mappings.

## Getting Started

Add the dependency to your pom file:

```xml
<dependency>
  <groupId>com.rapid7.docker</groupId>
  <artifactId>docker-image-analyzer</artifactId>
  <version>0.1.0</version>
</dependency>
```

_TODO: Usage example(s)_

## Development

Fork the repository and create a development branch in your fork. _Working from the master branch in your fork is not recommended._

1. Open your favorite IDE or text editor
2. Make some changes
3. Add some tests if needed
4. Run the tests
5. Push your changes
6. Open a pull request

You can use `mvn clean install` to clean compile, run checkstyle, and run all tests.

#### Code Style

docker-image-analyzer uses a variation of the Google Java code style, enforced with Checkstyle. Please make sure your changes adhere to this style before submitting a pull request.

## Testing

Run `mvn test` or `mvn clean install`.