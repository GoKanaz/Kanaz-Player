# Contributing to Kanaz Player

Thank you for your interest in contributing to Kanaz Player! This document provides guidelines and instructions for contributing to this project.

---

## Project Structure

Kanaz Player follows Clean Architecture principles to ensure scalability and maintainability.

### Directory Overview
```text
com.gokanaz.kanazplayer/
├── core/
│   ├── common/       # Common utilities and shared components
│   ├── data/         # Data layer (repositories, models, data sources)
│   ├── domain/       # Domain layer (use cases, repository interfaces)
│   └── di/           # Dependency injection modules
├── feature/          # Feature modules (player, library, etc.)
├── ui/               # UI components and theming
└── service/          # Background services

Architecture Rules
Domain Layer: Contains business logic and repository interfaces.
Data Layer: Implements repository interfaces and handles data sources.
Presentation Layer: UI components and ViewModels.
Dependencies flow: UI -> Domain <- Data.
Development Environment
Supported Environments
Android Studio (Recommended)
Termux with Android SDK (For headless/CLI development)
Prerequisites
Android SDK 34+
Java 17
Gradle 8.5+
Setting Up for Termux Development

# Clone the repository
git clone [https://github.com/gokanaz/kanaz-player.git](https://github.com/gokanaz/kanaz-player.git)
cd kanaz-player

# Build the project
./gradlew assembleDebug

# Run tests
./gradlew test


Contribution Workflow

1. Fork the Repository
   ```bash
   # Create your fork on GitHub
   # Clone your fork locally
   git clone https://github.com/YOUR_USERNAME/kanaz-player.git
   ```
2. Create a Feature Branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make Your Changes
   · Follow the existing code style
   · Write clear commit messages
   · Add tests for new functionality
   · Update documentation as needed
4. Commit Your Changes
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```
5. Push to Your Fork
   ```bash
   git push origin feature/your-feature-name
   ```
6. Create a Pull Request
   · Navigate to the original repository
   · Click "New Pull Request"
   · Select your feature branch
   · Fill out the PR template

Code Style Guidelines

Kotlin Style

· Use 4-space indentation
· Follow official Kotlin coding conventions
· Use descriptive variable and function names
· Prefer val over var when possible

Package Naming

All packages must be under com.gokanaz.kanazplayer:

```kotlin
// Correct
package com.gokanaz.kanazplayer.core.data.repository

// Incorrect
package com.example.kanazplayer.repository
```

Architecture Layers

· Data Layer: Use *RepositoryImpl suffix for implementations
· Domain Layer: Use *Repository interface, *UseCase for business logic
· Presentation Layer: Use *ViewModel, *Screen for UI components

Dependency Injection

· Use Hilt for dependency injection
· Follow the existing module structure in core/di/
· Use appropriate scopes (@Singleton, @ServiceScoped, etc.)

Testing

Unit Tests

· Place unit tests in src/test/ directory
· Use JUnit 4 and MockK for mocking
· Test ViewModels, UseCases, and Repository implementations

UI Tests

· Place UI tests in src/androidTest/ directory
· Use Compose testing APIs
· Test screen navigation and UI interactions

Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "*YourTestClass*"

# Run with coverage
./gradlew jacocoTestReport
```

Pull Request Guidelines

PR Title Format

```
type(scope): brief description

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Code style (formatting, etc.)
- refactor: Code refactoring
- test: Adding or updating tests
- chore: Maintenance tasks
```

PR Description

Include the following sections:

1. What does this PR do? - Brief description
2. Related Issue - Link to issue (if any)
3. Testing Performed - What you tested
4. Screenshots - For UI changes (if applicable)

Code Review Checklist

· Code follows project structure
· Tests are included and passing
· No lint warnings
· Documentation updated
· No breaking changes to existing functionality

Bug Reports

When reporting bugs, please include:

1. Environment: Android version, device model, Termux version (if applicable)
2. Steps to Reproduce: Clear, step-by-step instructions
3. Expected Behavior: What should happen
4. Actual Behavior: What actually happens
5. Logs: Any relevant error logs or stack traces

Feature Requests

For feature requests:

1. Check existing issues to avoid duplicates
2. Describe the feature in detail
3. Explain the use case and benefits
4. Consider if it aligns with project goals

Questions and Support

· For development questions: Open a GitHub Discussion
· For bugs: Open an Issue
· For security issues: Email the maintainer directly

Code of Conduct

All contributors are expected to adhere to the Contributor Covenant Code of Conduct.

License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to Kanaz Player! 🎵




