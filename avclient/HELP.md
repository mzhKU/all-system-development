# Calculate jgitver version:
mvn help:evaluate "-Dexpression=project.version" -q -DforceStdout

# Create annotated tag
mahed@LAPTOP-G1AQLNTL : avclient> git tag -a 1.0.1 -m "Release 1.0.1"
git push origin main --force --tags

# Getting Started
