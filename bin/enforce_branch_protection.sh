#!/bin/bash
# Enables branch protection (with required status check) and repository auto-merge,
# so that dependabot-auto-merge.yml can actually merge approved PRs.
# adapted from https://github.com/navikt/dagpenger/blob/master/bin/enforce_branch_protection.sh
#
# Run this once from the repository root:
#   ./bin/enforce_branch_protection.sh

set -e

# Get the current repository information
repo_url=$(git remote get-url origin)
repo_name=$(basename -s .git "$repo_url")
owner=$(echo "$repo_url" | awk -F"(/|:)" '{print $2}')

# Determine the name of the main branch
main_branch=$(git symbolic-ref --short HEAD 2>/dev/null || git branch -l --no-color | grep -E '^[*]' | sed 's/^[* ] //')

# Configure branch protection, and require the PR build to pass before merging.
# "Build and publish Docker image for PR-branch" matches the job name in
# .github/workflows/pull-requests.yml - update if that job is renamed.
echo '{
  "required_status_checks": {
    "strict": true,
    "checks": [
      { "context": "Build and publish Docker image for PR-branch" }
    ]
  },
  "enforce_admins": false,
  "required_pull_request_reviews": null,
  "required_conversation_resolution": true,
  "restrictions": null
}' | \
gh api repos/"$owner"/"$repo_name"/branches/"$main_branch"/protection \
  --method PUT \
  --silent \
  --header "Accept: application/vnd.github.v3+json" \
  --input -

# Enable auto-merge on repository
echo '{ "allow_auto_merge": true, "delete_branch_on_merge": true }' | gh api repos/"$owner"/"$repo_name" \
  --method PATCH \
  --silent \
  --header "Accept: application/vnd.github.v3+json" \
  --input -

if [ $? -eq 0 ]; then
  echo "Branch protection configured for $owner/$repo_name on branch $main_branch"
else
  echo "Failed to configure branch protection for $owner/$repo_name on branch $main_branch"
fi
