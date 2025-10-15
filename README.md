# FHIR Common Utils

This repository contains common utilities and models shared between the HAPI FHIR server and the FHIR core mobile client.

## Features

- Location hierarchy management utilities
- FHIR practitioner details models
- Common tree data structures for hierarchical data
- Utilities for FHIR resource handling

## Usage

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.smartregister</groupId>
    <artifactId>fhir-common-utils</artifactId>
    <version>1.0.3</version>
</dependency>
```

### Gradle

Add the following dependency to your `build.gradle`:

```gradle
implementation 'org.smartregister:fhir-common-utils:1.0.3'
```

## Development

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Building

```bash
mvn clean compile
```

### Testing

```bash
mvn test
```

### Building JAR

```bash
mvn clean package
```

## Release Process

This project is published to Maven Central. The release process is automated through GitHub Actions:

### Snapshot Releases

Snapshot releases are automatically deployed when tags matching the pattern `v*-SNAPSHOT` are pushed:

```bash
git tag v1.0.4-SNAPSHOT
git push origin v1.0.4-SNAPSHOT
```

### Production Releases

Production releases are automatically deployed to Maven Central when tags matching the pattern `v*` (without -SNAPSHOT) are pushed:

```bash
git tag v1.0.4
git push origin v1.0.4
```

### Release Candidates

Release candidates can be created using the pattern `v*-RC*`:

```bash
git tag v1.0.4-RC1
git push origin v1.0.4-RC1
```

## Configuration

### Required GitHub Secrets

The following secrets must be configured in your GitHub repository:

- `SETTINGS_XML`: Base64-encoded Maven settings.xml file containing Sonatype OSSRH credentials
- `GPG_PRIVATE_KEY`: Base64-encoded GPG private key for signing artifacts
- `GPG_PASSPHRASE`: Passphrase for the GPG private key

### Maven Settings.xml Template

Create a `settings.xml` file with the following structure:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>ossrh</id>
      <username>your-sonatype-username</username>
      <password>your-sonatype-password</password>
    </server>
  </servers>
</settings>
```

Then encode it as base64 and add it as the `SETTINGS_XML` secret:

```bash
base64 -i settings.xml
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For support, please open an issue in the GitHub repository or contact the maintainers.
