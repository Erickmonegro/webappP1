/* ==========================================================================
   1. CONTROL DEL MENÚ LATERAL (SIDEBAR)
   ========================================================================== */
const menuToggle = document.getElementById('menu-toggle');
const sidebar = document.getElementById('sidebar');

if (menuToggle && sidebar) {
    // Abrir/Cerrar
    menuToggle.addEventListener('click', (e) => {
        sidebar.classList.toggle('active');
        e.stopPropagation();
    });

    // Cerrar al hacer clic fuera
    document.addEventListener('click', (e) => {
        if (!sidebar.contains(e.target) && sidebar.classList.contains('active')) {
            sidebar.classList.remove('active');
        }
    });
}

/* ==========================================================================
   2. SISTEMA UNIVERSAL DE MODALES (Pop-ups)
   ========================================================================== */
// Función mágica para cerrar CUALQUIER modal
function closeAnyModal(modalElement) {
    if (modalElement) modalElement.classList.remove('active');
}

// Configurar el cierre universal (con la X, el botón cancelar o el fondo oscuro)
function setupModalClosing(modalId, closeBtnId, cancelBtnId) {
    const modal = document.getElementById(modalId);
    const closeBtn = document.getElementById(closeBtnId);
    const cancelBtn = document.getElementById(cancelBtnId);

    if (!modal) return; // Si el modal no existe en esta página, no hacemos nada

    // Cerrar con la X
    if (closeBtn) {
        closeBtn.addEventListener('click', () => closeAnyModal(modal));
    }

    // Cerrar con el botón Cancelar
    if (cancelBtn) {
        cancelBtn.addEventListener('click', (e) => {
            e.preventDefault();
            closeAnyModal(modal);
        });
    }

    // Cerrar haciendo clic en el fondo negro
    window.addEventListener('click', (e) => {
        if (e.target === modal) closeAnyModal(modal);
    });
}

// Inicializamos el sistema de cierre para los modales que ya tienes
setupModalClosing('taskModal', 'closeTaskModal', 'cancelTaskBtn');
setupModalClosing('docModal', 'closeDocModal', 'cancelDocBtn');
// El modal de booking no tiene botones de cancelar en tu diseño, por eso solo mandamos la ID principal para que sirva el clic en el fondo
setupModalClosing('bookingModal', null, null);


/* ==========================================================================
   3. LÓGICA ESPECÍFICA: TAREAS
   ========================================================================== */
const btnNewTask = document.getElementById('btnNewTask');
const taskModal = document.getElementById('taskModal');

if (btnNewTask && taskModal) {
    btnNewTask.addEventListener('click', () => {
        const taskForm = document.getElementById('newTaskForm');
        const hiddenId = document.getElementById('formTaskId');
        const modalTitle = document.querySelector('#taskModal .modal-header h3');

        if(taskForm) taskForm.reset();
        if(hiddenId) hiddenId.value = "";
        if(modalTitle) modalTitle.innerText = "Crear Nueva Tarea";

        taskModal.classList.add('active');
    });
}

const contextMenu = document.getElementById('customContextMenu');
const menuEditBtn = document.getElementById('menuEditBtn');
const menuDeleteForm = document.getElementById('menuDeleteForm');
let currentTaskData = {};

if (contextMenu) {
    document.querySelectorAll('.task-context').forEach(task => {
        task.addEventListener('contextmenu', function(e) {
            e.preventDefault();

            currentTaskData = {
                id: this.getAttribute('data-id'),
                titulo: this.getAttribute('data-titulo'),
                urgencia: this.getAttribute('data-urgencia'),
                fecha: this.getAttribute('data-fecha')
            };

            if(menuDeleteForm) menuDeleteForm.action = `/tareas/eliminar/${currentTaskData.id}`;

            contextMenu.style.left = `${e.pageX}px`;
            contextMenu.style.top = `${e.pageY}px`;
            contextMenu.classList.add('active');
        });
    });

    // Ocultar menú normal
    window.addEventListener('click', (e) => {
        if (!contextMenu.contains(e.target)) contextMenu.classList.remove('active');
    });

    // Acción Editar
    if(menuEditBtn) {
        menuEditBtn.addEventListener('click', () => {
            document.getElementById('formTaskId').value = currentTaskData.id || "";
            document.getElementById('formTaskTitle').value = currentTaskData.titulo || "";
            document.getElementById('formTaskUrgency').value = currentTaskData.urgencia || "MEDIA";

            const dateInput = document.getElementById('formTaskDate');
            if(dateInput && currentTaskData.fecha && currentTaskData.fecha !== 'null') {
                dateInput.value = currentTaskData.fecha;
            }

            document.querySelector('#taskModal .modal-header h3').innerText = "Editar Tarea";
            contextMenu.classList.remove('active');
            taskModal.classList.add('active');
        });
    }
}

/* ==========================================================================
   4. LÓGICA ESPECÍFICA: DOCUMENTOS
   ========================================================================== */
const btnUploadDoc = document.getElementById('btnUploadDoc');
const docModal = document.getElementById('docModal');

if (btnUploadDoc && docModal) {
    btnUploadDoc.addEventListener('click', () => {
        docModal.classList.add('active');
    });
}

document.querySelectorAll('.btn-delete').forEach(btn => {
    btn.addEventListener('click', function(e) {
        e.preventDefault();
        const docId = this.getAttribute('data-id');
        if (confirm('¿Estás seguro de eliminar este documento?')) {
            fetch(`/api/biblioteca/${docId}`, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    alert('Error al eliminar el documento.');
                }
            }).catch(err => console.error(err));
        }
    });
});

document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.addEventListener('click', function(e) {
        e.preventDefault();
        const docId = this.getAttribute('data-id');
        const tituloActual = this.closest('.doc-card').querySelector('.doc-title').innerText;
        const nuevoTitulo = prompt('Ingrese el nuevo título del documento:', tituloActual);
        
        if (nuevoTitulo && nuevoTitulo.trim() !== '' && nuevoTitulo !== tituloActual) {
            fetch(`/api/biblioteca/${docId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ titulo: nuevoTitulo.trim() })
            }).then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    alert('Error al actualizar el documento.');
                }
            }).catch(err => console.error(err));
        }
    });
});

/* ==========================================================================
   5. LÓGICA ESPECÍFICA: ALOJAMIENTOS (RESERVAS)
   ========================================================================== */
const bookingModal = document.getElementById('bookingModal');

// Esta función es llamada directamente desde el HTML: onclick="openBookingModal('Nombre', 150)"
window.openBookingModal = function(nombreAlojamiento, precio) {
    if(bookingModal) {
        document.getElementById('book-title').innerText = nombreAlojamiento;
        document.getElementById('book-price').innerText = "$" + precio + ".00";
        bookingModal.classList.add('active');
    }
}

// Función global de cerrar (por si la tienes en algún botón del HTML)
window.closeBookingModal = function() {
    closeAnyModal(bookingModal);
}