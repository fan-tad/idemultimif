/**
 * Created by guillaumemartinez on 17/10/2016.
 */
$(function () {
  $('#ide .tree-file').on('click', '.mdl-navigation__link.dir', function(){
    var $dir = $(`div[path="${$(this).attr('path')}"]`)
    if($dir.css('display') === "none"){
      $dir.show()
    } else {
      $dir.hide()
    }
  })

  /**
   * Handler create new file
   */
  $('#ide .mdl-layout__drawer > i').on('click', function () {
    promptNewFile("")
  })
  /**
   * show file options
   */
  $('#ide .tree-file').on('mouseenter', '.mdl-navigation__link.dir', function () {
    var self = this
    $(self).append(
      $('<i>').addClass('fa fa-plus new-file').attr('title', 'Ajouter un fichier').on('click', function () {
        promptNewFile($(self).attr('path'))
      })
    )
  })

  /**
   * show file options
   */
  $('#ide .tree-file').on('mouseenter', '.mdl-navigation__link', function () {
    var self = this
    $(self).append(
      $('<i>').addClass('fa fa-times remove-file').attr('title', 'Supprimer l\'élement').on('click', function () {
        if (confirm(`Supprimer ${$(self).attr('path')} ?`)) {
          deleteFile($(self).attr('path'), function () {
            getFolder("/", function (data) {
              createTree(data);
            })
          })
        }
      })
    )
  })
  /**
   * file hide options
   */
  $('#ide .tree-file').on('mouseleave', '.mdl-navigation__link', function () {
    $('#ide .tree-file .mdl-navigation__link .new-file, #ide .tree-file .mdl-navigation__link .remove-file').remove()
  })
  /**
   * Show selected file
   */
  $('#ide .tree-file').on('click', '.mdl-navigation__link', function () {
    $('#ide .tree-file .mdl-navigation__link').removeClass('selected')
    $(this).addClass('selected')
  })
  /**
   * Event to switch tabs IDE
   */
  $('#ide .tabs').on('click', 'a', function () {
    $('#ide .tabs a').removeClass('is-active');
    $('#ide .ide section').removeClass('is-active');
    $(this).addClass('is-active');
    $(`#ide .ide section#${$(this).attr('id')}`).addClass('is-active');
    $(`#ide .ide section#${$(this).attr('id')} code.result`).html(hljs.highlightAuto($(`#ide .ide section#${$(this).attr('id')} code.editor`).text()).value)
  });
  /**
   * Update code on the second ide
   */
  $('#ide .ide').on('mousedown mouseup keydown keyup', 'code.editor', function () {
    $('#ide .ide code.result').html(hljs.highlightAuto(this.innerText).value)
  });
  /**
   * Create a new file
   * @param path
   */
  function promptNewFile (path) {
    var fileName = prompt('File name:', '')
    if (fileName) {
      createFile(`${path}/${fileName}`, function () {
        getFolder("/", function (data) {
          createTree(data);
        })
      })
    }
  }

  /**
   * Open file in new tab
   * @param name
   */
  function openFile (name) {
    var printName = name.split('/').pop()
    printName = printName.replace(/[^a-zA-Z0-9]/g, '_')
    var tabs = $('#ide .tabs .mdl-layout__tab')
    for (var t = 0; t < tabs.length; t++) {
      var $tab = $(tabs[t])
      if ($tab.attr('id') == printName) {
        $(`#ide .tabs #${printName}`).trigger('click')
        return
      }
    }
    getFileContent(name, function (data) {
      createTab(name, data)
      $(`#ide .tabs #${printName}`).trigger('click')
    })
  }

  /**
   * Create new tab with file content
   * @param name
   * @param data
   */
  function createTab (name, data) {
    var path = name
    var printName = name.split('/').pop()
    name = printName.replace(/[^a-zA-Z0-9]/g, '_')
    // Add new tab in tabs menu
    $('#ide .tabs').append(
      $('<a>').addClass('mdl-layout__tab').attr('id', name).attr('href', `#${name}`).append(
        printName,
        $('<i>').addClass('fa fa-times').on('click', function () {
          $(`#ide .tabs #${name}`).remove();
          $(`#ide .ide #${name}`).remove()
        })
      )
    );
    // Append file content
    $('#ide .ide').append(
      $('<section>').addClass('mdl-layout__tab-panel').attr('id', name).append(
        $('<div>').addClass('page-content').append(
          $('<div>').addClass('actions').append(
            $('<i>').addClass('save fa fa-save').attr('id', name)
          ),
          $('<div>').addClass('editor').attr('id', `editor-${name}`)
        )
      )
    )
    var lang = printName.split('.').pop().toLowerCase()
    var editor = ace.edit(`editor-${name}`)
    editor.setTheme('ace/theme/monokai')
    editor.setValue(data)
    switch (lang) {
      case "c":
      case "cpp":
      case "c++":
      case "h":
      case "hpp":
      case "h++":
        editor.getSession().setMode("ace/mode/c_cpp")
        break
      case "java":
      case "class":
        editor.getSession().setMode("ace/mode/java")
        break
      case "json":
        editor.getSession().setMode("ace/mode/json")
        break
      case "xml":
        editor.getSession().setMode("ace/mode/xml")
        break
      default:
        editor.getSession().setMode("ace/mode/txt")
        break
    }
    $(`#ide .save#${name}`).on('click', function () {
      var data = editor.getValue()
      saveFile(path, data, function () {
        $.notify.success({
          title: 'Success',
          text: `Saving ${printName}`
        })
      })
    })
  }

  /**
   * Create tab with file
   * @param data
   */
  function createTree (data) {
    $('#ide .tree-file').empty()
    $('#ide .tree-file').append(
      generateTree(data, "")
    )
    componentHandler.upgradeDom()
  }

  /**
   * generate open file function
   * @param file
   * @param path
   * @returns {Function}
   */
  function generateOpenFileFunction (file, path) {
    return function () {
      if (file.type == "file") {
        openFile(`${path}/${file.name}`)
      }
    }
  }

  /**
   * Generate left folder tree
   */
  function generateTree (data, path) {
    var $folder = $('<div>').addClass("dir").attr('path', path);
    if(path !== ""){
      $folder.hide()
    }
    for (var f in data) {
      var file = data[f]
      if (file.name == '') {
        continue
      }
      $('<a>').addClass(`mdl-navigation__link ${file.type}`).attr('href', '#').attr('path', `${path}/${file.name}`).append(
        $('<i>').addClass(file.type === 'dir' ? 'fa fa-folder': 'fa fa-file').text(file.name)
      ).on('dblclick', generateOpenFileFunction(file, path)).appendTo($folder)
      if (file.type == "dir") {
        generateTree(file.child, `${path}/${file.name}`).appendTo($folder)
      }
    }
    return $folder
  }

  /**
   * Get list of folder's children
   * @param folder
   * @param onSuccess
   */
  function getFolder (folder, onSuccess) {
    API.get('/folder', {
      project: QueryString.project,
      path: folder
    }, onSuccess)
  }

  /**
   * Get the content of a file
   * @param file
   * @param onSuccess
   */
  function getFileContent (file, onSuccess) {
    API.get('/file', {
      project: QueryString.project,
      path: file
    }, onSuccess)
  }

  /**
   * Create file
   * @param file
   * @param onSuccess
   */
  function createFile (file, onSuccess) {
    API.post('/file', {
      project: QueryString.project,
      path: file,
      action: "create"
    }, onSuccess)
  }

  /**
   * Delete a file
   * @param file
   * @param onSuccess
   */
  function deleteFile (file, onSuccess) {
    API.post('/file', {
      project: QueryString.project,
      path: file,
      action: "delete"
    }, onSuccess)
  }

  /**
   * Save new content for a file
   * @param file
   * @param data
   * @param onSuccess
   */
  function saveFile (file, data, onSuccess) {
    API.post('/file', {
      project: QueryString.project,
      path: file,
      code: data,
      action: "save"
    }, onSuccess)
  }

  /**
   * Default call on load
   */
  getFolder("/", function (data) {
    createTree(data);
  })
  /**
   * Update tree every 5s
   */
  setTimeout(function () {
    getFolder("/", function (data) {
      createTree(data);
    })
  }, 5000)
  componentHandler.upgradeDom()
});
