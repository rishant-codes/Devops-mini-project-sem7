# Git Branching & Collaboration Workflow

Nothing here has been committed or pushed for you — these are the exact commands to run
yourself against `https://github.com/rishant-codes/DEVOPS-mini-project-23102B0065.git`
to satisfy the branching/PR/tag deliverables. Run them from the repo root
(`D:\Rishant Project\DEVOPS-mini-project-23102B0065`).

## Task 1 — Feature branch, PR, review, merge

```
git checkout -b feature/dashboard-api
git add 23102B0065-Mini-Proj/backend 23102B0065-Mini-Proj/frontend
git commit -m "feat: dashboard API, search, drill-down and alerts for station monitoring"
git push -u origin feature/dashboard-api
```

Then on GitHub:
1. Open a PR from `feature/dashboard-api` into `main` (or `development` if that's your
   base branch — create it first with `git checkout -b development && git push -u origin development`
   if it doesn't exist yet).
2. Add at least one review comment on a specific line (even on your own PR, GitHub lets
   you comment; or ask a classmate/second account to review).
3. **Merge** the PR (use "Create a merge commit" so the merge is visible in `git log --graph`).

Evidence to capture: the PR page (diff + comments), and the merged state
(`git log --oneline --graph` showing the merge commit).

## Task 2 — Second feature branch + conflict + tag

```
git checkout development
git checkout -b feature/mvp-completion
# (data entry / dashboard / alerts code already in 23102B0065-Mini-Proj/)
git add 23102B0065-Mini-Proj
git commit -m "feat: complete MVP - data entry, dashboard, summary, drill-down, alerts"
git push -u origin feature/mvp-completion
```

### Manufacturing a real merge conflict

To get a *genuine* conflict (not a fabricated one), edit the same line in two branches:

```
git checkout development
# edit 23102B0065-Mini-Proj/frontend/src/App.jsx header text, e.g. change the <h1> title
git commit -am "docs: tweak dashboard title on development"
git push

git checkout feature/mvp-completion
# edit the SAME <h1> line to something else
git commit -am "docs: tweak dashboard title on feature branch"
git push

# now try to merge development's change into your feature branch
git merge development
```

Git will report a conflict in `App.jsx`. Open the file, resolve the `<<<<<<<` /
`=======` / `>>>>>>>` markers, then:

```
git add 23102B0065-Mini-Proj/frontend/src/App.jsx
git commit -m "merge: resolve dashboard title conflict"
git push
```

### Tag the release

```
git checkout development
git merge feature/mvp-completion
git tag -a v1.0-mvp -m "Release-ready MVP: data entry, dashboard, alerts"
git push origin v1.0-mvp
```

Evidence to capture: the conflict markers before resolution (screenshot or
`git log --merge`), the resolved file, and `git tag` / the GitHub Releases/Tags page
showing `v1.0-mvp`.

## Notes

- `development` is used here as the integration branch per the assignment wording
  ("merge into the development branch"); create it once from `main` if it doesn't
  exist yet.
- Keep `main` as the stable/release branch — only merge `development` into `main` for
  the final release deliverable (Task 10).
