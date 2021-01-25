var tempSessionIdCached;

Plugin.register('skin2server', {
    title: 'Skin to Server!',
    author: 'lukeeey',
    description: 'Applies your skin in game with the click of a button',
    version: '1.0.0',
    variant: 'both',
    onload() {
        if (Format.id === 'skin') {
            MenuBar.addAction(new Action({
                id: 'connect_to_server',
                name: 'Connect to Minecraft Server',
                category: 'filter',
                click: function (ev) {
                    $.ajax({
                        type: 'GET',
                        url: 'http://localhost:8000/new-session'
                    }).done(function (data) {
                        tempSessionIdCached = data.sessionId
                        showConnectDialog(data.sessionId)
                    }).fail(function (error) {
                        alert("Error: " + JSON.stringify(error))
                    })
                }
            }), 'filter.0')

            MenuBar.addAction(new Action({
                id: 'export_skin_to_server',
                name: 'Apply Skin on Server',
                category: 'filter',
                click: function (ev) {
                   showExportDialog()
                }
            }), 'filter.0')
        }
    }
});

function showConnectDialog(sessionId) {
    var dialog = new Dialog({
        id: 'connect_to_server_dialog',
        title: 'Connect to Minecraft Server',
        lines: [
            'Enter a Minecraft server with the Skin2Server plugin instead and enter:',
            '<pre>/skin2server connect ' + sessionId + '</pre>',
        ],
        confirmEnabled: false
        // onConfirm: function (formData) {
        //     this.hide();
        // }
    }).show()

    var interval = setInterval(function () {
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8000/session/' + sessionId
        }).done(function (data) {
            if (data.connected) {
                dialog.hide()
                clearInterval(interval)
                onSessionJoined(data)
            }
        })
    }, 3 * 1000)
}

function showExportDialog() {
    var dialog = new Dialog({
        id: 'export_to_server_dialog',
        title: 'Apply Skin on Server',
        form: {
            entityUuid: {label: 'Entity UUID', type: 'input'} // TODO: Store last used entity id in local storage and auto fill,
        },
        onConfirm: function (formData) {
            $.ajax({
                type: 'POST',
                url: 'http://localhost:8000/session/' + tempSessionIdCached + '/export',
                data: {
                    entityUuid: formData.entityUuid,
                    texture: textures[0].img.src
                }
            }).done(function (data) {
                alert('Export complete!')
            }).fail(function (error) {
                alert('Error: ' + JSON.stringify(error))
            })
        }
    }).show()
}

function onSessionJoined(data) {

}