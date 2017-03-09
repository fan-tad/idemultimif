/**
 * Created by guillaumemartinez on 17/10/2016.
 */
$(function () {
  var Markdown = new markdownit();
  /**
   * Save wiki
   */
  $('#wiki .save').on('click', function () {
    saveWiki($('#wiki textarea').val());
    $('#wiki textarea').hide()
  });
  /**
   * Open the wiki editor
   */
  $('#wiki .edit').on('click', function () {
    if ($('#wiki textarea').css('display') === 'none') {
      $('#wiki textarea').show()
    } else {
      $('#wiki textarea').hide()
    }
  });
  /**
   * Update wiki overview on editing it
   */
  $('#wiki textarea').bind('input propertychang', function () {
    $('pre.wiki code').html(Markdown.renderInline($(this).val()));
    hljs.highlightBlock($('pre.wiki code').get(0))
  });
  /**
   * Append content to wiki viewer
   * @param markdown
   */
  function appendWiki (markdown) {
    $('pre.wiki code').html(Markdown.renderInline(markdown));
    hljs.highlightBlock($('pre.wiki code').get(0));
    $('#wiki textarea').text(markdown)
  }

  /**
   * Get wiki content
   */
  function getWiki () {
    API.get('/wiki', {
      project: QueryString.project
    }, function (data) {
      appendWiki(data.text)
    })
  }

  /**
   * Save wiki new content
   * @param data
   */
  function saveWiki (data) {
    API.post('/wiki', {
      project: QueryString.project,
      text: data
    }, function () {
      $.notify.success({
        title: 'Success',
        text: `Saving wiki`
      })
    })
  }

  /**
   * Default call on load
   */
  getWiki()
});
