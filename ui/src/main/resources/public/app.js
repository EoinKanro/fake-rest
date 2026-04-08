const DEFAULT_HANDLER_CONFIG = {
    id: '',
    path: '',
    method: 'GET',
    type: 'STATIC',
    responseBody: '',
    responseCode: 200,
    groovyCode: '',
    routerPath: ''
};

// ── Elements ──────────────────────────────────────────────

const popupSettingsHandler = document.getElementById('settings-handler');
const textHandlerId = document.getElementById('h-id');
const textHandlerPath = document.getElementById('h-path');
const dropdownHandlerMethod = document.getElementById('h-method');
const dropdownHandlerType = document.getElementById('h-type');
const textHandlerResponseBody = document.getElementById('h-response-body');
const numberHandlerResponseCode = document.getElementById('h-response-code');
const textHandlerGroovyCode = document.getElementById('h-groovy-code');
const textHandlerRouterPath = document.getElementById('h-router-path');

const buttonHandlerDelete = document.getElementById('button-handler-delete');

const popupSettingsServer  = document.getElementById('settings-server');
const numberServerMockPort = document.getElementById('s-mock-port');
const numberServerUiPort   = document.getElementById('s-ui-port');

const fieldsStatic = document.querySelectorAll('.field-static');
const fieldsGroovy = document.querySelectorAll('.field-groovy');
const fieldsRouter = document.querySelectorAll('.field-router');

// ── Handlers ─────────────────────────────────────────────

function openHandlerSettings(config) {
    textHandlerId.value = config.id;
    textHandlerPath.value = config.path;
    dropdownHandlerMethod.value = config.method;
    dropdownHandlerType.value = config.type;
    textHandlerResponseBody.value = config.responseBody;
    numberHandlerResponseCode.value = config.responseCode;
    textHandlerGroovyCode.value = config.groovyCode;
    textHandlerRouterPath.value = config.routerPath;

    refreshHandlerTypeFields(config.type);
    buttonHandlerDelete.style.display = config.id ? '' : 'none';
    popupSettingsHandler.style.display = 'flex';
}

function closeHandlerSettings() {
    popupSettingsHandler.style.display = 'none';
}

function refreshHandlerTypeFields(type) {
    fieldsStatic.forEach(el => el.style.display = type === 'STATIC' ? '' : 'none');
    fieldsGroovy.forEach(el => el.style.display = type === 'GROOVY' ? '' : 'none');
    fieldsRouter.forEach(el => el.style.display = type === 'ROUTER' ? '' : 'none');
}

// ── Server settings ───────────────────────────────────────

function openServerSettings(config) {
    numberServerMockPort.value = config.mockPort;
    numberServerUiPort.value   = config.uiPort;

    popupSettingsServer.style.display = 'flex';
}

function closeServerSettings() {
    popupSettingsServer.style.display = 'none';
}

// ── Listeners ────────────────────────────────────────────

document.getElementById('button-add-handler').addEventListener('click', () => {
    openHandlerSettings(DEFAULT_HANDLER_CONFIG);
});
document.getElementById('button-handler-cancel').addEventListener('click', closeHandlerSettings);
dropdownHandlerType.addEventListener('change', () => refreshHandlerTypeFields(dropdownHandlerType.value));

document.getElementById('button-settings').addEventListener('click', openServerSettings);
document.getElementById('button-server-cancel').addEventListener('click', closeServerSettings);
