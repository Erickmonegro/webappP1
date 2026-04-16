const house_container = document.getElementById('house_container')
const search_input = document.getElementById("search_input")
const walletAmount = document.querySelector(".wallet-amount")
const walletAmountReservation = document.getElementById("wallet-amount-reservation")
const reservationsContainer = document.getElementById('reservations-container')

let alojamientos = []
let currentUser = null

// ─── Cargar créditos del usuario ────────────────────────────────────────────
async function loadUserCredits() {
    try {
        const res = await fetch("/api/me")
        if (res.status === 401 || res.status === 403) {
            window.location.href = 'login.html'
            return
        }
        currentUser = await res.json()
        updateWalletDisplay(currentUser.creditos)
    } catch (err) {
        console.error("Error al cargar créditos:", err)
    }
}

function updateWalletDisplay(creditos) {
    const formatted = `RD$ ${creditos.toLocaleString('es-DO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
    if (walletAmount) walletAmount.textContent = formatted
    if (walletAmountReservation) walletAmountReservation.textContent = formatted
}

// ─── Cargar alojamientos ─────────────────────────────────────────────────────
async function loadAlojamientos() {
    const res = await fetch("/api/alojamiento")
    if (res.status == 401 || res.status == 403) {
        window.location.href = 'login.html'
        return
    }
    alojamientos = await res.json()
}

// ─── Render de tarjetas ──────────────────────────────────────────────────────
function renderAlojamiento(lista) {
    house_container.innerHTML = ""

    if (lista.length === 0) {
        house_container.style.cssText = 'display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:260px;'
        house_container.innerHTML = `
            <div style="display:flex;flex-direction:column;align-items:center;text-align:center;">
                <span class="material-symbols-outlined" style="font-size:64px;margin-bottom:16px;color:rgba(255,255,255,0.25);">house</span>
                <h3 style="font-size:1.3rem;font-weight:700;color:rgba(255,255,255,0.5);margin:0 0 8px;">No hay alojamientos disponibles</h3>
                <p style="font-size:0.9rem;color:rgba(255,255,255,0.3);max-width:300px;line-height:1.6;margin:0;">
                    En este momento no hay alojamientos disponibles.
                </p>
            </div>
        `
        return
    }

    house_container.style.cssText = ''

    lista.forEach(alojamiento => {
        house_container.innerHTML += `
        <div class="aloj-card">
            <div class="aloj-card-image">
                <span class="material-symbols-outlined">house</span>
            </div>
            <div class="aloj-card-body">
                <h4>${alojamiento.nombre}</h4>
                <div class="aloj-card-location">
                    <span class="material-symbols-outlined">location_on</span>
                    ${alojamiento.descripcion}
                </div>
                <div class="aloj-card-price">
                    $${(alojamiento.precioPorDia * 30).toFixed(2)} <span>/ mes</span>
                </div>
                <button class="aloj-btn-book" onclick="openBookingModal(${alojamiento.id}, '${alojamiento.nombre}', ${alojamiento.precioPorDia * 30})">Reservar Ahora</button>
            </div>
        </div>
    `
    })
}

// ─── Tabs ─────────────────────────────────────────────────────────────────────
window.switchTab = function(tab) {
    const panelDisponibles = document.getElementById('panel-disponibles')
    const panelReservas    = document.getElementById('panel-reservas')
    const tabDisponibles   = document.getElementById('tab-disponibles')
    const tabReservas      = document.getElementById('tab-reservas')

    if (tab === 'disponibles') {
        panelDisponibles.style.display = 'block'
        panelReservas.style.display    = 'none'
        tabDisponibles.classList.add('active')
        tabReservas.classList.remove('active')
    } else {
        panelDisponibles.style.display = 'none'
        panelReservas.style.display    = 'block'
        tabDisponibles.classList.remove('active')
        tabReservas.classList.add('active')
    }
}

// ─── Búsqueda ────────────────────────────────────────────────────────────────
search_input.addEventListener("input", () => {
    const term = search_input.value.toLowerCase()
    const result = alojamientos.filter(a =>
        (a.nombre?.toLowerCase() || "").includes(term) ||
        (a.descripcion?.toLowerCase() || "").includes(term)
    )
    renderAlojamiento(result)
})

// ─── Modal de Reserva (mensual) ───────────────────────────────────────────────
let bookingAlojamientoId = null
let bookingPrecioMensual = 0

const bookingMsg = document.getElementById('booking-msg')
const confirmBtn = document.getElementById('confirmPaymentBtn')

window.openBookingModal = function(alojamientoId, nombre, precioMensual) {
    bookingAlojamientoId = alojamientoId
    bookingPrecioMensual = precioMensual
    document.getElementById('book-title').textContent = nombre
    document.getElementById('book-price').textContent = `$${precioMensual.toFixed(2)}`
    showBookingMsg('', '')
    document.getElementById('bookingModal').classList.add('active')
}

window.closeBookingModal = function() {
    document.getElementById('bookingModal').classList.remove('active')
}

// Confirmar reserva mensual
confirmBtn.addEventListener('click', async () => {
    if (!currentUser) {
        showBookingMsg('No se pudo identificar al usuario. Recarga la página.', 'error')
        return
    }

    confirmBtn.disabled = true
    confirmBtn.textContent = 'Procesando...'

    try {
        const payload = {
            userId:        currentUser.id,
            alojamientoId: bookingAlojamientoId
        }

        const res = await fetch('/api/reservacion', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        })

        if (res.status === 402) {
            showBookingMsg('❌ Créditos insuficientes para realizar esta reserva.', 'error')
            return
        }

        const data = await res.json()

        if (res.ok) {
            currentUser.creditos -= bookingPrecioMensual
            updateWalletDisplay(currentUser.creditos)
            loadMisReservaciones()
            showBookingMsg('✅ ¡Reserva confirmada exitosamente!', 'success')
            setTimeout(() => closeBookingModal(), 1800)
        } else {
            const msg = typeof data === 'string' ? data : (data.message ?? 'Intenta de nuevo.')
            showBookingMsg(`❌ Error: ${msg}`, 'error')
        }
    } catch (err) {
        console.error(err)
        showBookingMsg('❌ Error de conexión. Intenta de nuevo.', 'error')
    } finally {
        confirmBtn.disabled = false
        confirmBtn.textContent = 'Confirmar y Pagar'
    }
})

function showBookingMsg(text, type) {
    if (!bookingMsg) return
    if (!text) { bookingMsg.style.display = 'none'; return }
    bookingMsg.textContent = text
    bookingMsg.style.display = 'block'
    bookingMsg.style.background = type === 'success' ? '#14532d' : '#450a0a'
    bookingMsg.style.color      = type === 'success' ? '#86efac'  : '#fca5a5'
}

// ─── Mis Reservas ────────────────────────────────────────────────────────────
async function loadMisReservaciones() {
    try {
        const res = await fetch('/api/reservacion/me')
        if (res.status === 401 || res.status === 403) return
        const reservaciones = await res.json()
        renderMisReservaciones(reservaciones)
    } catch (err) {
        console.error("Error al cargar reservas:", err)
    }
}

function renderMisReservaciones(reservaciones) {
    if (!reservationsContainer) return

    if (reservaciones.length === 0) {
        reservationsContainer.innerHTML = `
            <div style="display:flex;flex-direction:column;align-items:center;text-align:center;min-height:200px;justify-content:center;opacity:0.5;">
                <span class="material-symbols-outlined" style="font-size:56px;color:rgba(255,255,255,0.3);margin-bottom:12px;">bookmark</span>
                <h3 style="color:rgba(255,255,255,0.5);font-size:1.1rem;margin:0 0 6px;">No tienes reservas activas</h3>
                <p style="color:rgba(255,255,255,0.3);font-size:0.88rem;margin:0;">Explora la lista y reserva tu alojamiento.</p>
            </div>
        `
        return
    }

    reservationsContainer.innerHTML = ''

    reservaciones.forEach(r => {
        const nombre  = r.accommodation?.nombre ?? 'Alojamiento'
        const entrada = r.fechaEntrada ?? '---'
        const salida  = r.fechaSalida  ?? '---'
        const total   = r.precioTotal != null ? `$${r.precioTotal.toFixed(2)}` : '---'

        reservationsContainer.innerHTML += `
        <div class="aloj-card">
            <div class="aloj-card-image">
                <span class="material-symbols-outlined" style="color:#60a5fa;">verified</span>
            </div>
            <div class="aloj-card-body">
                <h4>${nombre}</h4>
                <div class="aloj-card-location">
                    <span class="material-symbols-outlined">calendar_today</span>
                    ${entrada} → ${salida}
                </div>
                <div class="aloj-card-price" style="color:#4ade80;">
                    ${total} <span>pagado</span>
                </div>
                <span class="aloj-badge-reservado">✓ Reservado</span>
            </div>
        </div>
    `
    })
}

// ─── Init ────────────────────────────────────────────────────────────────────
async function init() {
    await Promise.all([loadAlojamientos(), loadUserCredits(), loadMisReservaciones()])
    renderAlojamiento(alojamientos)
}

init()