# Continuous Testing Demonstration

This is the walkthrough for the "Continuous Testing in Jenkins" deliverable: a failed
pipeline caused by a real defect, a fix commit, and a successful rerun.

## Step 1 — run the pipeline green first

Run the Jenkins job once with `RUN_SELENIUM=true` and confirm all 5 Selenium tests pass
(JUnit plugin shows 5/5). This is your baseline.

## Step 2 — introduce a deliberate defect

Edit `frontend/src/components/AlertsPanel.jsx` and change the alert threshold so it no
longer flags the seeded Industrial Park reading (154 AQI):

```diff
- const alerts = stations.filter((s) => s.latestAqi != null && s.latestAqi > 100)
+ const alerts = stations.filter((s) => s.latestAqi != null && s.latestAqi > 200)
```

Commit this on a branch (e.g. `bugfix/alert-threshold-defect`) and push it — this is
your "deliberately introduced defect" commit.

## Step 3 — watch it fail

Trigger the pipeline against that branch/commit. `AlertDisplayTest` (which asserts
Industrial Park appears in the alerts panel) fails because the panel is now empty. The
Jenkinsfile's `Continuous Testing - Selenium` stage has:

```groovy
post {
    failure {
        error('Selenium quality gate failed - blocking deployment.')
    }
}
```

so the build goes **red** and the `Docker Build` / `Docker Push` / `Deploy` stages never
run — capture a screenshot of this failed build and the JUnit failure detail for your
deliverable.

## Step 4 — fix the defect and rerun

Revert the threshold back to `100`, commit with a message like
`fix: restore AQI alert threshold to 100`, and push. Trigger the pipeline again (or wait
for the 5-minute poll) — all stages, including Docker build and deploy, should now go
green.

## What to capture as evidence

1. Screenshot of the passing baseline build (Step 1).
2. The defect commit's diff/hash (Step 2).
3. Screenshot of the failed build + JUnit report showing `AlertDisplayTest` failed
   (Step 3).
4. The fix commit's diff/hash (Step 4).
5. Screenshot of the successful rerun (Step 4).
