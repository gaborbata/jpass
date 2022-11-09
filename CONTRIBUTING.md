# Contributing to JPass

We love your input! We want to make contributing to this project as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

## We Develop with Github

We use Github to host code, to track issues and feature requests, as well as accept pull requests.

## We Use Github Flow, So All Code Changes Happen Through Pull Requests

Pull requests are the best way to propose changes to the codebase
(we use [Github Flow](https://docs.github.com/en/get-started/quickstart/github-flow)).

A pull request should be small and preferably should contain only one feature.

1. Fork the repo and create your branch from `master`.
2. Ensure the code compiles and the resulting `jar` package works as intended on different environments.
   * `gradle clean build` is preferred, but you can use `mvn clean package` as well
3. If you've added code that should be tested, add tests.
   * Make sure any non-UI related code has at least 80% code coverage
4. If you've changed configuration, update the documentation.
5. Ensure the test suite passes (`gradle test`).
6. Make sure your code lints (`gradle check`).
7. Issue that pull request!

## License

In short, when you submit code changes, your submissions are understood to be under
the same [License](https://github.com/gaborbata/jpass/blob/master/LICENSE) that covers the project.

If your change includes a licensed material, please update the `LICENSE`.

## Report bugs using Github's issues

We use GitHub issues to track public bugs.
Report a bug by [opening a new issue](https://github.com/gaborbata/jpass/issues); it's that easy!

## Write bug reports with detail, background, and sample code

**Great Bug Reports** tend to have:

- A quick summary and/or background
- Application version and environment (Java version, operating system)
- Steps to reproduce
  - Be specific!
  - Give sample code if you can.
- What you expected would happen
- What actually happens
- Notes (possibly including why you think this might be happening, or stuff you tried that didn't work)

People *love* thorough bug reports. I'm not even kidding.

## Use a Consistent Coding Style

This project has a Checkstyle configuration mostly based on
[Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

* 4 spaces for indentation rather than tabs
* You can try running `gradle check` for validation
