/* ============================================================
   admin.js  –  ROOMA / webappP1
   Endpoints:
     GET    /api/alojamiento        → lista todos los alojamientos
     POST   /api/alojamiento        → crea un alojamiento
     PUT    /api/alojamiento/{id}   → edita un alojamiento
     DELETE /api/alojamiento/{id}   → elimina un alojamiento
     GET    /api/users              → lista todos los usuarios (solo lectura)
   ============================================================ */

// ─── ESTADO ────────────────────────────────────────────────
let alojamientos = []
let usuarios     = []
let eliminarId   = null

// ─── REFERENCIAS DOM ──────────────────────────────────────
const tabBtns              = document.querySelectorAll('.tab')
const panelAlojamientos    = document.getElementById('panel-alojamientos')
const panelUsuarios        = document.getElementById('panel-usuarios')

const tablaAlojamientos    = document.getElementById('tabla-alojamientos')
const bodyTablaAlojamientos= document.getElementById('body-tabla-alojamientos')
const emptyAlojamientos    = document.getElementById('empty-alojamientos')
const searchAlojamiento    = document.getElementById('searchAlojamiento')

const tablaUsuarios        = document.getElementById('tabla-usuarios')
const bodyTablaUsuarios    = document.getElementById('body-tabla-usuarios')
const emptyUsuarios        = document.getElementById('empty-usuarios')
const searchUsuario        = document.getElementById('searchUsuario')

const statTotal            = document.getElementById('totalAlojamientos')
const statDisponibles      = document.getElementById('totalDisponibles')
const statNoDisponibles    = document.getElementById('totalNoDisponibles')

// Modal de alojamiento
const modalAlojamiento     = document.getElementById('modal-alojamiento')
const formAlojamiento      = document.getElementById('form-alojamiento')
const modalTitulo          = document.getElementById('modal-alojamiento-titulo')
const inputId              = document.getElementById('alojamiento-id')
const inputNombre          = document.getElementById('alojamiento-nombre')
const inputPiso            = document.getElementById('alojamiento-piso')
const inputPrecio          = document.getElementById('alojamiento-precio')
const inputDescripcion     = document.getElementById('alojamiento-descripcion')
const inputDisponible      = document.getElementById('alojamiento-disponible')
const formMsg              = document.getElementById('form-alojamiento-msg')
const btnGuardar           = document.getElementById('btn-guardar-alojamiento')

// Modal de confirmación
const modalConfirmar       = document.getElementById('modal-confirmar')
const confirmNombre        = document.getElementById('confirm-nombre')


// ─── API HELPERS ──────────────────────────────────────────
async function apiFetch(url, options = {}) {
    const res = await fetch(url, {
        credentials: 'include',
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
        ...options
    })
    if (res.status === 401 || res.status === 403) {
        window.location.href = '/login'
        throw new Error('No autenticado')
    }
    return res
}


// ─── FETCH DATA ──────────────────────────────────────────
async function cargarAlojamientos() {
    try {
        const res = await apiFetch('/api/alojamiento')
        alojamientos = await res.json()
        actualizarStats()
        renderAlojamientos(alojamientos)
    } catch (err) {
        console.error('Error cargando alojamientos:', err)
    }
}

async function cargarUsuarios() {
    try {
        const res = await apiFetch('/api/users')
        if (!res.ok) {
            console.error('Error cargando usuarios:', res.status)
            return
        }
        usuarios = await res.json()
        renderUsuarios(usuarios)
    } catch (err) {
        console.error('Error cargando usuarios:', err)
    }
}


// ─── STATS ───────────────────────────────────────────────
function actualizarStats() {
    const total      = alojamientos.length
    const disponibles = alojamientos.filter(a => a.disponible).length
    statTotal.textContent       = total
    statDisponibles.textContent = disponibles
    statNoDisponibles.textContent = total - disponibles
}


// ─── RENDER ALOJAMIENTOS ──────────────────────────────────
function renderAlojamientos(datos) {
    bodyTablaAlojamientos.innerHTML = ''

    if (!datos || datos.length === 0) {
        emptyAlojamientos.style.display = 'block'
        tablaAlojamientos.style.display = 'none'
        return
    }

    emptyAlojamientos.style.display = 'none'
    tablaAlojamientos.style.display = 'table'

    datos.forEach(a => {
        const tr = document.createElement('tr')
        const disponibleBadge = a.disponible
            ? `<span class="badge badge-true">✓ Sí</span>`
            : `<span class="badge badge-false">✗ No</span>`

        tr.innerHTML = `
            <td>${a.id}</td>
            <td><strong>${a.nombre}</strong></td>
            <td>${a.piso ?? '–'}</td>
            <td>$${parseFloat(a.precioPorDia).toFixed(2)}</td>
            <td style="max-width:220px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${a.descripcion ?? '–'}</td>
            <td>${disponibleBadge}</td>
            <td>
                <div class="action-group">
                    <button class="btn-action btn-edit"   data-id="${a.id}">✏️ Editar</button>
                    <button class="btn-action btn-delete" data-id="${a.id}" data-nombre="${a.nombre}">🗑️ Eliminar</button>
                </div>
            </td>
        `
        bodyTablaAlojamientos.appendChild(tr)
    })
}


// ─── RENDER USUARIOS ─────────────────────────────────────
function renderUsuarios(datos) {
    bodyTablaUsuarios.innerHTML = ''

    if (!datos || datos.length === 0) {
        emptyUsuarios.style.display = 'block'
        tablaUsuarios.style.display = 'none'
        return
    }

    emptyUsuarios.style.display = 'none'
    tablaUsuarios.style.display = 'table'

    datos.forEach(u => {
        const rol = (u.role || u.rol || '–').toString().toLowerCase()
        const tr = document.createElement('tr')
        tr.innerHTML = `
            <td>${u.id}</td>
            <td>${u.nombre}</td>
            <td>${u.email}</td>
            <td><span class="badge badge-${rol}">${u.role || u.rol || '–'}</span></td>
            <td>${u.creditos != null ? '$' + parseFloat(u.creditos).toFixed(2) : '–'}</td>
        `
        bodyTablaUsuarios.appendChild(tr)
    })
}


// ─── TABS ────────────────────────────────────────────────
tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        tabBtns.forEach(b => b.classList.remove('active'))
        btn.classList.add('active')

        const target = btn.id.replace('tab-', 'panel-')
        document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'))
        document.getElementById(target).classList.add('active')

        if (target === 'panel-usuarios' && usuarios.length === 0) {
            cargarUsuarios()
        }
    })
})


// ─── BUSCADORES ──────────────────────────────────────────
searchAlojamiento.addEventListener('input', () => {
    const q = searchAlojamiento.value.trim().toLowerCase()
    if (!q) { renderAlojamientos(alojamientos); return }
    renderAlojamientos(
        alojamientos.filter(a =>
            (a.nombre      || '').toLowerCase().includes(q) ||
            (a.descripcion || '').toLowerCase().includes(q) ||
            String(a.piso).includes(q)
        )
    )
})

searchUsuario.addEventListener('input', () => {
    const q = searchUsuario.value.trim().toLowerCase()
    if (!q) { renderUsuarios(usuarios); return }
    renderUsuarios(
        usuarios.filter(u =>
            (u.nombre || '').toLowerCase().includes(q) ||
            (u.email  || '').toLowerCase().includes(q)
        )
    )
})


// ─── ABRIR MODAL (CREAR) ─────────────────────────────────
document.getElementById('btn-nuevo-alojamiento').addEventListener('click', () => {
    abrirModalAlojamiento(null)
})

function abrirModalAlojamiento(alojamiento) {
    formAlojamiento.reset()
    setMsg('', '')

    if (alojamiento) {
        modalTitulo.textContent     = 'Editar Alojamiento'
        btnGuardar.textContent      = 'Guardar cambios'
        inputId.value               = alojamiento.id
        inputNombre.value           = alojamiento.nombre
        inputPiso.value             = alojamiento.piso ?? ''
        inputPrecio.value           = alojamiento.precioPorDia
        inputDescripcion.value      = alojamiento.descripcion ?? ''
        inputDisponible.checked     = alojamiento.disponible
    } else {
        modalTitulo.textContent     = 'Nuevo Alojamiento'
        btnGuardar.textContent      = 'Crear'
        inputId.value               = ''
        inputDisponible.checked     = true
    }

    modalAlojamiento.classList.add('active')
}

function cerrarModalAlojamiento() {
    modalAlojamiento.classList.remove('active')
}

document.getElementById('btn-cerrar-modal-alojamiento').addEventListener('click', cerrarModalAlojamiento)
document.getElementById('btn-cancelar-alojamiento').addEventListener('click', cerrarModalAlojamiento)

modalAlojamiento.addEventListener('click', e => {
    if (e.target === modalAlojamiento) cerrarModalAlojamiento()
})


// ─── SUBMIT FORM (CREAR / EDITAR) ─────────────────────────
formAlojamiento.addEventListener('submit', async e => {
    e.preventDefault()

    const nombre = inputNombre.value.trim()
    const piso   = inputPiso.value.trim()
    const precio = inputPrecio.value.trim()

    if (!nombre || !piso || !precio) {
        setMsg('Nombre, piso y precio son obligatorios.', 'error')
        return
    }

    const payload = {
        nombre:       nombre,
        piso:         parseInt(piso),
        precioPorDia: parseFloat(precio),
        descripcion:  inputDescripcion.value.trim(),
        disponible:   inputDisponible.checked
    }

    const id = inputId.value
    const esEdicion = !!id

    btnGuardar.disabled    = true
    btnGuardar.textContent = 'Guardando…'

    try {
        const res = await apiFetch(
            esEdicion ? `/api/alojamiento/${id}` : '/api/alojamiento',
            {
                method: esEdicion ? 'PUT' : 'POST',
                body: JSON.stringify(payload)
            }
        )

        if (!res.ok) {
            const err = await res.text()
            setMsg(`Error: ${err}`, 'error')
            return
        }

        const data = await res.json()

        if (esEdicion) {
            alojamientos = alojamientos.map(a => a.id == id ? data : a)
        } else {
            alojamientos.push(data)
        }

        actualizarStats()
        renderAlojamientos(alojamientos)
        cerrarModalAlojamiento()

    } catch (err) {
        console.error(err)
        setMsg('No se pudo conectar con el servidor.', 'error')
    } finally {
        btnGuardar.disabled = false
        btnGuardar.textContent = esEdicion ? 'Guardar cambios' : 'Crear'
    }
})


// ─── CLICK EN TABLA (EDITAR / ELIMINAR) ───────────────────
bodyTablaAlojamientos.addEventListener('click', async e => {
    const btnEdit   = e.target.closest('.btn-edit')
    const btnDel    = e.target.closest('.btn-delete')

    if (btnEdit) {
        const id = btnEdit.dataset.id
        const alojamiento = alojamientos.find(a => a.id == id)
        if (alojamiento) abrirModalAlojamiento(alojamiento)
    }

    if (btnDel) {
        eliminarId = btnDel.dataset.id
        confirmNombre.textContent = `"${btnDel.dataset.nombre}"`
        modalConfirmar.classList.add('active')
    }
})


// ─── MODAL CONFIRMAR ELIMINACIÓN ─────────────────────────
document.getElementById('btn-cancelar-eliminar').addEventListener('click', () => {
    modalConfirmar.classList.remove('active')
    eliminarId = null
})

modalConfirmar.addEventListener('click', e => {
    if (e.target === modalConfirmar) {
        modalConfirmar.classList.remove('active')
        eliminarId = null
    }
})

document.getElementById('btn-confirmar-eliminar').addEventListener('click', async () => {
    if (!eliminarId) return

    const btnConf = document.getElementById('btn-confirmar-eliminar')
    btnConf.disabled    = true
    btnConf.textContent = 'Eliminando…'

    try {
        const res = await apiFetch(`/api/alojamiento/${eliminarId}`, { method: 'DELETE' })

        if (res.status === 204 || res.ok) {
            alojamientos = alojamientos.filter(a => a.id != eliminarId)
            actualizarStats()
            renderAlojamientos(alojamientos)
            modalConfirmar.classList.remove('active')
            eliminarId = null
        } else {
            alert('No se pudo eliminar el alojamiento.')
        }
    } catch (err) {
        console.error(err)
        alert('Error al conectar con el servidor.')
    } finally {
        btnConf.disabled    = false
        btnConf.textContent = 'Sí, eliminar'
    }
})


// ─── HELPERS ─────────────────────────────────────────────
function setMsg(text, type) {
    formMsg.textContent  = text
    formMsg.className    = `form-msg ${type}`
}


// ─── INIT ────────────────────────────────────────────────
cargarAlojamientos()