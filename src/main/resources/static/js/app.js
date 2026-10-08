/* Utilidades comunes del frontend: sesión, menú por permisos, llamadas a la API con CSRF y alertas. */
const App = (() => {

  function leerCookie(nombre) {
    const m = document.cookie.match(new RegExp('(?:^|; )' + nombre + '=([^;]*)'));
    return m ? decodeURIComponent(m[1]) : null;
  }

  function esc(v) {
    return String(v ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  }

  /** fetch a la API: envía el token CSRF, maneja 401/403 y convierte ApiError en Error legible. */
  async function api(url, { method = 'GET', body } = {}) {
    const headers = { 'Accept': 'application/json' };
    if (body !== undefined) headers['Content-Type'] = 'application/json';
    const token = leerCookie('XSRF-TOKEN');
    if (token && method !== 'GET') headers['X-XSRF-TOKEN'] = token;

    const resp = await fetch(url, {
      method, headers, credentials: 'same-origin',
      body: body !== undefined ? JSON.stringify(body) : undefined
    });

    if (resp.status === 401) {
      window.location.href = '/login.html?expirada';
      throw new Error('Sesión expirada');
    }
    if (resp.status === 204) return null;

    const texto = await resp.text();
    let data = null;
    try { data = texto ? JSON.parse(texto) : null; } catch (e) { data = texto; }

    if (!resp.ok) {
      let msg = (data && data.mensaje) || (typeof data === 'string' && data) || `Error ${resp.status}`;
      if (data && data.errores) msg += '<ul class="mb-0 mt-1">' +
        Object.entries(data.errores).map(([k, v]) => `<li><b>${esc(k)}</b>: ${esc(v)}</li>`).join('') + '</ul>';
      const err = new Error(msg);
      err.status = resp.status;
      throw err;
    }
    return data;
  }

  function alerta(mensaje, tipo = 'success', contenedor = 'alertContainer') {
    const c = document.getElementById(contenedor);
    if (!c) return;
    c.innerHTML = `<div class="alert alert-${tipo} alert-dismissible fade show mb-3" role="alert">
        ${mensaje}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>`;
    c.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  function fechaHora(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    return isNaN(d) ? iso : d.toLocaleString('es-PE', { dateStyle: 'short', timeStyle: 'short' });
  }

  function logout() {
    const f = document.createElement('form');
    f.method = 'POST';
    f.action = '/logout';
    f.innerHTML = `<input type="hidden" name="_csrf" value="${esc(leerCookie('XSRF-TOKEN'))}">`;
    document.body.appendChild(f);
    f.submit();
  }

  /**
   * Arma el menú lateral y la cabecera con los módulos que el backend dice que el usuario puede usar.
   * Si el usuario no tiene el módulo de la página actual, lo redirige (el backend también lo bloquea).
   */
  async function iniciarLayout(codigoModulo, titulo, subtitulo = '') {
    let sesion;
    try {
      sesion = await api('/api/auth/me');
    } catch (e) {
      return null;
    }
    if (codigoModulo && !sesion.modulos.some(m => m.codigo === codigoModulo)) {
      window.location.href = '/403.html';
      return null;
    }

    const items = sesion.modulos.map(m => `
      <li><a href="${m.ruta}" class="${m.codigo === codigoModulo ? 'active' : ''}">
        <i class="bi ${esc(m.icono)}"></i> ${esc(m.nombre)}</a></li>`).join('');

    document.getElementById('sidebar').innerHTML = `
      <div class="sidebar-brand"><i class="bi bi-hospital"></i> MedicalSys</div>
      <ul class="sidebar-menu">
        <li><a href="/index.html" class="${codigoModulo ? '' : 'active'}"><i class="bi bi-house-door"></i> Inicio</a></li>
        <li class="sidebar-section">Módulos</li>
        ${items}
      </ul>`;

    const iniciales = sesion.nombreCompleto.split(' ').slice(0, 2).map(p => p[0]).join('').toUpperCase();
    document.getElementById('topHeader').innerHTML = `
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-outline-secondary btn-sm btn-menu" type="button" aria-label="Menú"
                onclick="document.getElementById('sidebar').classList.toggle('abierto')"><i class="bi bi-list"></i></button>
        <div>
          <h5 class="m-0 fw-bold text-dark" id="pageTitle">${esc(titulo)}</h5>
          <small class="text-muted">${esc(subtitulo)}</small>
        </div>
      </div>
      <div class="user-chip">
        <div class="user-avatar">${esc(iniciales)}</div>
        <div class="lh-sm">
          <div class="fw-semibold">${esc(sesion.nombreCompleto)}</div>
          <span class="badge badge-rol">${esc(sesion.rol)}</span>
        </div>
        <button class="btn btn-outline-danger btn-sm ms-2" onclick="App.logout()" title="Cerrar sesión">
          <i class="bi bi-box-arrow-right"></i><span class="d-none d-md-inline"> Salir</span></button>
      </div>`;

    document.body.classList.remove('cargando');
    return sesion;
  }

  return { api, alerta, esc, fechaHora, logout, iniciarLayout, leerCookie };
})();
