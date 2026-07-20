#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

failed=0

for file in paths/*.md capabilities/*.md; do
  steps=$(grep -c '^## Step ' "$file")
  locations=$(grep -c '^> 📍 ' "$file")
  checks=$(grep -c '^Before continuing, check:' "$file")
  if [[ "$steps" -ne "$locations" || "$steps" -ne "$checks" ]]; then
    echo "$file: steps=$steps locations=$locations checks=$checks"
    failed=1
  fi
done

for project in taskboard-api starters/*; do
  [[ -d "$project" ]] || continue
  for required in pom.xml mvnw mvnw.cmd README.md src/main/java src/test/java; do
    if [[ ! -e "$project/$required" ]]; then
      echo "$project: missing $required"
      failed=1
    fi
  done
done

perl -MFile::Basename=dirname -MFile::Spec -e '
  $bad = 0;
  for $file (@ARGV) {
    open $handle, "<", $file or die "$file: $!";
    $line = 0;
    while (<$handle>) {
      $line++;
      while (/\[[^\]]*\]\(([^)]+)\)/g) {
        $path = $1;
        $path =~ s/#.*$//;
        next if $path eq "" || $path =~ m{^(?:https?://|mailto:)};
        $path =~ s/^<|>$//g;
        $full = File::Spec->canonpath(File::Spec->catfile(dirname($file), $path));
        if (!-e $full) {
          print "$file:$line: missing link target $path\n";
          $bad = 1;
        }
      }
    }
  }
  exit $bad;
' README.md paths/*.md capabilities/*.md docs/*.md starters/*.md starters/*/README.md taskboard-api/README.md || failed=1

perl -e '
  $bad = 0;
  for $file (@ARGV) {
    open $handle, "<", $file or die "$file: $!";
    $fences = 0;
    while (<$handle>) { $fences++ if /^```/; }
    if ($fences % 2) {
      print "$file: unmatched code fence ($fences fences)\n";
      $bad = 1;
    }
  }
  exit $bad;
' README.md paths/*.md capabilities/*.md docs/*.md starters/*.md starters/*/README.md taskboard-api/README.md || failed=1

if grep -RInE '^\*\*(Where|Physically do|Verify|What|Do now|Finish this step when|Go next):' \
    paths capabilities docs; then
  echo "Old process labels remain"
  failed=1
fi

if [[ "$failed" -ne 0 ]]; then
  exit 1
fi

echo "Handbook validation passed."
