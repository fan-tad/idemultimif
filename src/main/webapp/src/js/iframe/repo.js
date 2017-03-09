/**
 * Created by guillaumemartinez on 17/10/2016.
 */
$(function () {
  var gitTemplateConfig = {
    colors: ['#32C8DE', '#27AE60', '#F7A253', '#F75353'], // branches colors, 1 per column
    branch: {
      lineWidth: 4,
      spacingX: 30,
      showLabel: true, // display branch names on graph
    },
    commit: {
      spacingY: -40,
      dot: {
        size: 5
      },
      message: {
        displayAuthor: true,
        displayBranch: false,
        displayHash: true,
        font: 'normal 12pt Helvetica'
      }
    }
  };
  var gitTemplate = new GitGraph.Template(gitTemplateConfig);
  var gitGraph = new GitGraph({
    template: gitTemplate,
    orientation: 'vertical-reverse',
    mode: 'extended'
  });
  var branches = {};

  /**
   * Get commit list from server
   * @param branch
   * @param number
   */
  function getCommits (branch, number, onsuccess) {
    API.get('/git/commits', {
      project: QueryString.project,
      branch: branch,
      number: number
    }, onsuccess)
  }

  /**
   * Draw commit tree
   * @param commits
   */
  function drawTree (commits) {
    commits.forEach(function (commit) {
      if (branches[commit.branch] == null) {
        branches[commit.branch] = gitGraph.branch(commit.branch)
      }
      switch (commit.action) {
        case 'commit':
          branches[commit.branch].commit({
            sha1: commit.hash,
            message: commit.message,
            author: commit.author
          });
          break;
        case 'fork':
          branches[commit.branch].checkout();
          branches[commit.forkChild] = gitGraph.branch(commit.forkChild);
          break;
        case 'merge':
          branches[commit.branch].merge(branches[commit.mergeOn], `Merge ${commit.branch} on ${commit.mergeOn}`);
          break;
        default:
          break
      }
    })
  }

  /**
   * Default call on load
   */
  getCommits('', 100, function (commits) {
    drawTree(commits)
  })
});
