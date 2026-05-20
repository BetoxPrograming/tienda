# 🤝 Contributing Guide

All contributions, bug reports, bug fixes, documentation improvements, enhancements, and ideas are welcome.

> [!IMPORTANT]
> This repository is part of the university course **SC-403 Desarrollo de Aplicaciones Web y Patrones** [*SC-403 Web Application Development and Patterns*].  
> Before contributing, please read the [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md) and [`LICENSE`](LICENSE) files.

---

## 📌 General Contribution Rules

This repository is mainly an academic learning portfolio for the **tienda** [*store*] project.

Contributions should respect the course structure, the professor's guidelines, and the academic purpose of the repository.

| Rule | Description |
|---|---|
| Keep the academic context | Changes must support the learning process of the course. |
| Respect the project structure | Do not move folders or files without a clear reason. |
| Document important changes | Any relevant change should be explained in commits or documentation. |
| Avoid unnecessary complexity | Keep the code understandable for a university learning project. |
| Protect credentials | Do not upload passwords, tokens, API keys, or private configuration files. |

---

## 💡 Suggestions and Ideas

Suggestions are welcome when they improve clarity, documentation, structure, or learning value.

Good suggestions may include:

- Better documentation structure
- Clearer code comments
- Improved folder organization
- More readable examples
- Small UI or UX improvements
- Safer configuration practices

> [!NOTE]
> Suggestions should not replace the course methodology unless the change is clearly documented as a personal adaptation.

---

## 🔀 Pull Request Guide

When creating a pull request, please follow this structure:

| Step | Action |
|---|---|
| 1 | Create a branch with a clear name. |
| 2 | Make focused changes. |
| 3 | Test the project when applicable. |
| 4 | Update documentation if needed. |
| 5 | Open the pull request with a short explanation. |

Recommended branch names:

```text
docs/update-readme
feat/product-crud
fix/login-validation
refactor/service-layer
chore/project-setup
```

Pull requests should explain:

```text
What changed?
Why was it changed?
How was it tested or reviewed?
```

> [!TIP]
> For academic weekly progress, keep changes small and easy to review.

---
## 🌿 Lesson Work Branch

This repository uses a permanent working branch called `lesson-work`.

The purpose of this branch is to manage weekly class progress in an organized way without working directly on `main`.

| Branch | Purpose |
|---|---|
| `main` | Stable branch. It contains reviewed, completed, and approved updates. |
| `lesson-work` | Active branch used only for weekly class work, class notes, and course-related code changes. |

The `lesson-work` branch is reused every week and should not be deleted after each merge.

---

### 📌 Purpose of `lesson-work`

The `lesson-work` branch is used only for changes made during weekly class sessions.

This branch may include:

| Allowed Change | Description |
|---|---|
| Class code changes | New or modified source code files created during class. |
| Weekly notes | Files added or edited inside `docs/weekly-notes/`. |
| Class-specific documentation | Documentation directly related to a weekly lesson or topic. |
| Professor-guided implementation | Files required by the implementation developed during class. |

This branch helps keep a clear separation between:

```text
Course material taught in class
Personal experiments, improvements, or extra implementations
```

---

### 🚫 Changes Not Recommended in `lesson-work`

Avoid using `lesson-work` for changes that are not directly related to weekly class progress.

Do not use this branch for:

| Avoid | Reason |
|---|---|
| General README changes | These should be handled in a separate documentation branch. |
| Repository-wide documentation restructuring | This is not weekly class work. |
| Repository configuration changes | These should be handled in a `chore/` branch. |
| Personal experiments | These should be handled in a separate feature or experiment branch. |
| Extra implementations outside the class scope | These should be clearly separated from professor-guided content. |

Recommended branch examples for changes outside class work:

```text
docs/update-readme
docs/reorganize-documentation
feat/personal-improvement
refactor/project-structure
chore/repository-config
```

> [!IMPORTANT]
> `lesson-work` should represent what is being developed, practiced, or documented during class.  
> Changes outside the weekly class scope should be managed in a separate branch.

---

### ✅ Weekly Class Commit Rules

Commits made from `lesson-work` should clearly describe what changed during class.

The commit should make clear:

```text
What changed?
Which week or class topic does it belong to?
Was it code, documentation, correction, or refactoring?
```

Recommended examples:

```text
✨ feat(lesson): add week 02 product controller
📝 docs(lesson): add week 02 class notes
🐛 fix(lesson): correct week 03 Thymeleaf route
♻️ refactor(lesson): organize week 04 service logic
```

Bad examples:

```text
update
class
changes
week work
final
```

---

### 🔄 Weekly Workflow

```bash
# Switch to the permanent class working branch
git switch lesson-work

# Update lesson-work with the latest remote changes
git pull

# Work on weekly class code, notes, or documentation

# Review modified files before committing
git status

# Add only the files related to the weekly class work
git add .

# Commit the weekly class progress with a clear message
git commit -m "✨ feat(lesson): add week XX class progress"

# Push the lesson-work branch to GitHub
git push
```

When the weekly progress is complete and reviewed, open a Pull Request from:

```text
lesson-work → main
```

After merging into `main`, keep using `lesson-work` for the next weekly class session.

---
## 🧾 Git Commit Message Style

Use this commit format:

```text
<emoji> <type>(<scope>): <action> <object>
```

Examples:

```text
📝 docs(readme): add course project context
✨ feat(product): add product listing page
🐛 fix(cart): correct total calculation
♻️ refactor(service): simplify product service logic
🙈 chore(gitignore): add local environment exclusions
```

---

## 🏷️ Commit Types

| Type | Use it for |
|---|---|
| `docs` | README files, notes, guides, documentation |
| `feat` | New functionality |
| `fix` | Bug fixes or corrections |
| `refactor` | Internal code improvements without changing behavior |
| `style` | Formatting, spacing, visual adjustments |
| `test` | Tests or testing documentation |
| `chore` | Maintenance tasks, configs, project setup |
| `build` | Build system, dependencies, Maven changes |
| `ci` | Continuous integration or deployment workflows |

---

## ✅ Commit Rules

Commit messages should follow these rules:

| Rule | Example |
|---|---|
| Write in English | `docs(readme): add project overview` |
| Use present tense | `add`, not `added` |
| Use imperative mood | `fix login error`, not `fixes login error` |
| Keep the first line under 72 characters | Short and readable |
| Use a clear scope | `readme`, `product`, `cart`, `user`, `security` |
| Avoid vague messages | Do not use only `update`, `changes`, or `final version` |

Bad examples:

```text
update
changes
final version
things fixed
project stuff
```

Good examples:

```text
📝 docs(readme): add weekly update schedule
✨ feat(product): add product entity model
🐛 fix(user): correct role validation logic
```

---

## 🧩 Extended Commit Description

For important changes, use an extended commit description.

Format:

```text
<emoji> <type>(<scope>): <action> <object>

Explain what changed and why it matters.
Mention any relevant academic, technical, or documentation context.
```

Example:

```text
📝 docs(readme): add academic repository overview

Document the academic purpose, course context, project scope,
planned technologies, weekly update schedule, and current status
of the tienda repository.
```

---

## 📚 Documentation Style

Documentation should be clear, structured, and easy to scan.

Use:

- Headings
- Short paragraphs
- Tables for comparisons or rules
- Code blocks for commands, formulas, and examples
- GitHub alerts for important notes
- Emojis only when they improve readability

Recommended documentation folders:

```text
docs/
├── weekly-notes/
│   ├── week-01.md
│   ├── week-02.md
│   └── week-03.md
├── design-decisions/
│   └── README.md
├── class-notes/
│   └── README.md
└── screenshots/
    └── README.md
```

---

## 🎨 Visual Style Rules

Keep the repository readable and consistent.

| Element | Rule |
|---|---|
| Emojis | Use moderately and consistently. |
| Headings | Use clear section titles. |
| Tables | Use them for rules, technologies, or comparisons. |
| Code blocks | Use them for commands and examples. |
| Alerts | Use them for important notes, warnings, or tips. |

> [!WARNING]
> Avoid overdecorating documentation. Visual elements should improve clarity, not distract from the content.

---

## 🧪 Testing Notes

When code changes are made, verify the project before committing when possible.

Suggested checks:

```bash
./mvnw clean install
```

or on Windows:

```bash
mvnw.cmd clean install
```

Also verify manually when applicable:

- The application starts correctly.
- The modified page loads without errors.
- The database connection works if the change depends on persistence.
- Forms, buttons, and links behave as expected.
- No credentials or private files were added by mistake.

> [!NOTE]
> Testing requirements may change as the repository grows during the course.

---

## 📝 Additional Notes

This repository is a work in progress.

Some code may follow the professor's instructional examples closely because the repository is part of a university learning process. Personal changes, improvements, or alternative decisions should be documented clearly.

The main goal is to show consistent progress, organized learning, and responsible use of Git and GitHub throughout the course.
