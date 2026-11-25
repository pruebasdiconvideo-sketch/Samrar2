/* ============================================================
   Smart Power On S.A.C. — JavaScript de aplicación
   Componentes reutilizables:
     · SPO.buscadorModal  → búsqueda + selección en ventana modal
                            (reemplaza a los <select> / combobox)
     · SPO.paginacion     → paginación cliente cuando una tabla
                            supera los 100 registros
     · SPO.toast          → notificaciones flotantes
   Requiere: Bootstrap 5.3.3 (bundle) y Bootstrap Icons.
   ============================================================ */
(function () {
  "use strict";

  const SPO = (window.SPO = window.SPO || {});

  /* ---------------------------------------------------------
     Toast / notificaciones
     --------------------------------------------------------- */
  SPO.toast = function (mensaje, tipo = "primary", duracion = 3500) {
    let cont = document.querySelector(".spo-toast-container");
    if (!cont) {
      cont = document.createElement("div");
      cont.className = "spo-toast-container";
      document.body.appendChild(cont);
    }
    const iconos = {
      primary: "bi-info-circle-fill",
      success: "bi-check-circle-fill",
      danger: "bi-x-octagon-fill",
      warning: "bi-exclamation-triangle-fill",
    };
    const el = document.createElement("div");
    el.className = `spo-toast toast toast-${tipo} show`;
    el.setAttribute("role", "alert");
    el.innerHTML = `
      <div class="toast-body d-flex align-items-center gap-2">
        <i class="bi ${iconos[tipo] || iconos.primary}"></i>
        <span class="small">${mensaje}</span>
        <button type="button" class="btn-close btn-close-white ms-auto" data-bs-dismiss="toast"></button>
      </div>`;
    cont.appendChild(el);
    const instancia = bootstrap.Toast.getOrCreateInstance(el, { delay: duracion });
    instancia.show();
    el.addEventListener("hidden.bs.toast", () => el.remove());
  };

  /* ---------------------------------------------------------
     Paginación de tablas (solo aparece si > porPagina registros)
     --------------------------------------------------------- */
  const Paginacion = (SPO.paginacion = {});

  Paginacion.config = {}; // por id de tabla

  /**
   * Inicializa la paginación de una tabla.
   * @param {string} tablaId            id del <table>
   * @param {object} opts
   *   - porPagina   {number}  registros por página (def: 100)
   *   - buscarInput {string}  id de un <input> de texto para filtrar
   *   - buscarEn    {number[]} índices de columnas donde buscar (def: todas)
   */
  Paginacion.init = function (tablaId, opts = {}) {
    const cfg = {
      porPagina: opts.porPagina || 100,
      pagina: 1,
      buscarInput: opts.busarInput || opts.buscarInput || null,
      buscarEn: opts.buscarEn || null,
      extraFiltro: typeof opts.extraFiltro === "function" ? opts.extraFiltro : null,
    };
    Paginacion.config[tablaId] = cfg;

    const tabla = document.getElementById(tablaId);
    if (!tabla) return;
    // Contenedor de controles
    let controles = tabla.parentNode.querySelector(".spo-paginacion");
    if (!controles) {
      controles = document.createElement("div");
      controles.className = "spo-paginacion d-none";
      tabla.parentNode.appendChild(controles);
    }

    if (cfg.buscarInput) {
      const inp = document.getElementById(cfg.buscarInput);
      if (inp) {
        inp.addEventListener("input", () => {
          cfg.pagina = 1;
          Paginacion._render(tablaId);
        });
      }
    }
    Paginacion._render(tablaId);
  };

  Paginacion.refrescar = function (tablaId) {
    Paginacion._render(tablaId);
  };

  Paginacion._filasVisibles = function (tablaId) {
    const cfg = Paginacion.config[tablaId];
    const tabla = document.getElementById(tablaId);
    if (!tabla) return [];
    let filas = Array.from(tabla.tBodies[0].querySelectorAll("tr"));
    const term = cfg.buscarInput
      ? (document.getElementById(cfg.buscarInput)?.value || "").trim().toLowerCase()
      : "";
    if (term) {
      filas = filas.filter((tr) => {
        if (tr.dataset.spoIgnore) return false;
        const celdas = Array.from(tr.children);
        const cols = cfg.buscarEn
          ? cfg.buscarEn.map((i) => celdas[i]).filter(Boolean)
          : celdas;
        return cols.some((c) => (c.textContent || "").toLowerCase().includes(term));
      });
    }
    if (cfg.extraFiltro) {
      filas = filas.filter((tr) => !tr.dataset.spoIgnore && cfg.extraFiltro(tr));
    }
    return filas;
  };

  Paginacion._render = function (tablaId) {
    const cfg = Paginacion.config[tablaId];
    const tabla = document.getElementById(tablaId);
    if (!tabla) return;
    const controles = tabla.parentNode.querySelector(".spo-paginacion");
    if (!controles) return;

    const todas = Array.from(tabla.tBodies[0].querySelectorAll("tr"));
    const visibles = Paginacion._filasVisibles(tablaId);
    const total = visibles.length;

    // Ocultar todas primero; mostrar solo las de la página actual
    todas.forEach((tr) => (tr.style.display = "none"));
    const totalPaginas = Math.max(1, Math.ceil(total / cfg.porPagina));
    if (cfg.pagina > totalPaginas) cfg.pagina = 1;
    const inicio = (cfg.pagina - 1) * cfg.porPagina;
    const fin = Math.min(inicio + cfg.porPagina, total);
    visibles.slice(inicio, fin).forEach((tr) => (tr.style.display = ""));

    // Si no supera el umbral (y no hay búsqueda/filtro activo), ocultar controles
    const hayBusqueda =
      cfg.buscarInput &&
      (document.getElementById(cfg.buscarInput)?.value || "").trim() !== "";
    const totalSinFiltro = todas.filter((tr) => !tr.dataset.spoIgnore).length;
    const filtroActivo = total !== totalSinFiltro;
    if (total <= cfg.porPagina && !hayBusqueda && !filtroActivo) {
      controles.classList.add("d-none");
      controles.innerHTML = "";
      // Sin filtros: mostrar todas las filas válidas
      todas.forEach((tr) => {
        if (!tr.dataset.spoIgnore) tr.style.display = "";
      });
      return;
    }
    controles.classList.remove("d-none");

    const info = `Mostrando ${total === 0 ? 0 : inicio + 1}–${fin} de ${total} registro(s)`;
    let html = `<div class="spo-page-info">${info}</div>`;

    // Botón primero + anterior
    html += `<ul class="pagination mb-0">
      <li class="page-item ${cfg.pagina === 1 ? "disabled" : ""}">
        <a class="page-link" href="#" data-spo-pag="1" aria-label="Primera">&laquo;</a>
      </li>
      <li class="page-item ${cfg.pagina === 1 ? "disabled" : ""}">
        <a class="page-link" href="#" data-spo-pag="${cfg.pagina - 1}" aria-label="Anterior">&lsaquo;</a>
      </li>`;

    // Rango de páginas (máx 7 visibles)
    let start = Math.max(1, cfg.pagina - 3);
    let end = Math.min(totalPaginas, start + 6);
    start = Math.max(1, end - 6);
    if (start > 1) {
      html += `<li class="page-item disabled"><span class="page-link">…</span></li>`;
    }
    for (let p = start; p <= end; p++) {
      html += `<li class="page-item ${p === cfg.pagina ? "active" : ""}">
        <a class="page-link" href="#" data-spo-pag="${p}">${p}</a>
      </li>`;
    }
    if (end < totalPaginas) {
      html += `<li class="page-item disabled"><span class="page-link">…</span></li>`;
    }

    html += `<li class="page-item ${cfg.pagina === totalPaginas ? "disabled" : ""}">
        <a class="page-link" href="#" data-spo-pag="${cfg.pagina + 1}" aria-label="Siguiente">&rsaquo;</a>
      </li>
      <li class="page-item ${cfg.pagina === totalPaginas ? "disabled" : ""}">
        <a class="page-link" href="#" data-spo-pag="${totalPaginas}" aria-label="Última">&raquo;</a>
      </li>
    </ul>`;

    controles.innerHTML = html;
    controles.querySelectorAll("a[data-spo-pag]").forEach((a) => {
      a.addEventListener("click", (e) => {
        e.preventDefault();
        const p = parseInt(a.getAttribute("data-spo-pag"), 10);
        if (!isNaN(p) && p >= 1 && p <= totalPaginas) {
          cfg.pagina = p;
          Paginacion._render(tablaId);
          tabla.scrollIntoView({ behavior: "smooth", block: "nearest" });
        }
      });
    });
  };

  /* ---------------------------------------------------------
     Buscador modal: búsqueda + selección en ventana modal
     Reemplaza a los <select> / combobox de selección de entidades.
     --------------------------------------------------------- */
  const Buscador = (SPO.buscadorModal = {});

  Buscador._estado = {
    config: null,
    datos: [],
    filtrados: [],
    pagina: 1,
    porPagina: 8,
    modal: null,
    termino: "",
  };

  /**
   * Abre el modal de búsqueda y selección.
   * @param {object} opts
   *   - titulo      {string}
   *   - icono       {string}  clase bi-* para el encabezado
   *   - url         {string}  endpoint REST que devuelve un array (opcional)
   *   - datos       {array}   datos estáticos (si no hay url)
   *   - placeholder {string}
   *   - columnas    {array}   [{titulo, campo, render(item)->html, clase}]
   *   - buscarEn    {array}   [campos] donde buscar el término
   *   - vacio       {string}  mensaje cuando no hay resultados
   *   - alSeleccionar(item)   callback
   */
  Buscador.abrir = async function (opts) {
    const est = Buscador._estado;
    est.config = opts;
    est.pagina = 1;
    est.termino = "";

    const modalEl = document.getElementById("buscadorModal");
    if (!modalEl) {
      console.error("Falta el elemento #buscadorModal en la página.");
      return;
    }

    // Encabezado
    modalEl.querySelector(".modal-title").innerHTML = `<i class="bi ${opts.icono || "bi-search"}"></i> ${opts.titulo || "Buscar"}`;
    modalEl.querySelector(".buscador-input").setAttribute("placeholder", opts.placeholder || "Escriba para buscar...");
    modalEl.querySelector(".buscador-input").value = "";

    // Cabecera de la tabla de resultados
    const thead = modalEl.querySelector(".resultado-buscador thead tr");
    thead.innerHTML = (opts.columnas || [])
      .map((c) => `<th class="${c.clase || ""}">${c.titulo}</th>`)
      .join("");

    // Cargar datos
    let datos = opts.datos || [];
    if (opts.url) {
      try {
        modalEl.querySelector(".buscador-cargando").classList.remove("d-none");
        const res = await fetch(opts.url, { headers: { Accept: "application/json" } });
        if (!res.ok) throw new Error("HTTP " + res.status);
        datos = await res.json();
      } catch (e) {
        SPO.toast("No se pudieron cargar los datos: " + e.message, "danger");
        datos = [];
      } finally {
        modalEl.querySelector(".buscador-cargando").classList.add("d-none");
      }
    }
    est.datos = Array.isArray(datos) ? datos : [];
    // Filtro opcional sobre el conjunto de datos cargado (p.ej. por proyecto)
    if (typeof opts.filtroDatos === "function") {
      est.datos = est.datos.filter((item) => opts.filtroDatos(item));
    }
    Buscador._filtrar();
    est.modal = bootstrap.Modal.getOrCreateInstance(modalEl);
    est.modal.show();

    // Focus al input cuando el modal termine de abrirse
    modalEl.addEventListener("shown.bs.modal", function onShown() {
      modalEl.querySelector(".buscador-input").focus();
      modalEl.removeEventListener("shown.bs.modal", onShown);
    });
  };

  Buscador._filtrar = function () {
    const est = Buscador._estado;
    const cfg = est.config;
    const term = est.termino.trim().toLowerCase();
    est.filtrados = !term
      ? est.datos.slice()
      : est.datos.filter((item) =>
          (cfg.buscarEn || []).some((campo) =>
            String(Buscador._valor(item, campo) ?? "").toLowerCase().includes(term),
          ),
        );
    est.pagina = 1;
    Buscador._renderResultados();
  };

  Buscador._valor = function (obj, ruta) {
    return String(ruta)
      .split(".")
      .reduce((acc, k) => (acc == null ? acc : acc[k]), obj);
  };

  Buscador._renderResultados = function () {
    const est = Buscador._estado;
    const cfg = est.config;
    const modalEl = document.getElementById("buscadorModal");
    const tbody = modalEl.querySelector(".resultado-buscador tbody");
    const info = modalEl.querySelector(".buscador-info");
    const pag = modalEl.querySelector(".buscador-pag");
    const total = est.filtrados.length;
    const totalPaginas = Math.max(1, Math.ceil(total / est.porPagina));
    if (est.pagina > totalPaginas) est.pagina = 1;
    const inicio = (est.pagina - 1) * est.porPagina;
    const fin = Math.min(inicio + est.porPagina, total);
    const pagina = est.filtrados.slice(inicio, fin);

    if (total === 0) {
      tbody.innerHTML = `<tr class="buscador-vacio"><td colspan="${(cfg.columnas || []).length}">${cfg.vacio || "Sin resultados."}</td></tr>`;
      info.textContent = "";
      pag.innerHTML = "";
      return;
    }

    tbody.innerHTML = pagina
      .map(
        (item, i) =>
          `<tr data-spo-idx="${inicio + i}">${(cfg.columnas || [])
            .map((c) => {
              const val = c.render
                ? c.render(item)
                : Buscador._escapeHtml(String(Buscador._valor(item, c.campo) ?? ""));
              return `<td class="${c.clase || ""}">${val}</td>`;
            })
            .join("")}</tr>`,
      )
      .join("");

    info.textContent = `${total === 0 ? 0 : inicio + 1}–${fin} de ${total} resultado(s)`;

    // Paginación del modal
    let html = "";
    const rango = (n) => {
      let s = Math.max(1, est.pagina - 2);
      let e = Math.min(n, s + 4);
      s = Math.max(1, e - 4);
      return Array.from({ length: e - s + 1 }, (_, i) => s + i);
    };
    html += `<li class="page-item ${est.pagina === 1 ? "disabled" : ""}"><a class="page-link" href="#" data-bpag="${est.pagina - 1}">&lsaquo;</a></li>`;
    rango(totalPaginas).forEach((p) => {
      html += `<li class="page-item ${p === est.pagina ? "active" : ""}"><a class="page-link" href="#" data-bpag="${p}">${p}</a></li>`;
    });
    html += `<li class="page-item ${est.pagina === totalPaginas ? "disabled" : ""}"><a class="page-link" href="#" data-bpag="${est.pagina + 1}">&rsaquo;</a></li>`;
    pag.innerHTML = `<ul class="pagination mb-0">${html}</ul>`;

    pag.querySelectorAll("a[data-bpag]").forEach((a) => {
      a.addEventListener("click", (e) => {
        e.preventDefault();
        const p = parseInt(a.getAttribute("data-bpag"), 10);
        if (!isNaN(p) && p >= 1 && p <= totalPaginas) {
          est.pagina = p;
          Buscador._renderResultados();
        }
      });
    });

    tbody.querySelectorAll("tr[data-spo-idx]").forEach((tr) => {
      tr.addEventListener("click", () => {
        const idx = parseInt(tr.getAttribute("data-spo-idx"), 10);
        const item = est.filtrados[idx];
        if (cfg.alSeleccionar) cfg.alSeleccionar(item);
        est.modal.hide();
      });
    });
  };

  Buscador._escapeHtml = function (s) {
    return s
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  };

  // Inicializar listeners del modal una sola vez
  document.addEventListener("DOMContentLoaded", function () {
    const modalEl = document.getElementById("buscadorModal");
    if (!modalEl) return;
    const input = modalEl.querySelector(".buscador-input");
    let timer = null;
    input.addEventListener("input", () => {
      clearTimeout(timer);
      timer = setTimeout(() => {
        Buscador._estado.termino = input.value;
        Buscador._filtrar();
      }, 180);
    });
    modalEl.querySelector(".buscador-limpiar").addEventListener("click", () => {
      input.value = "";
      Buscador._estado.termino = "";
      Buscador._filtrar();
      input.focus();
    });
  });

  /* ---------------------------------------------------------
     Helper: campo buscador (input readonly + botón lupa)
     Devuelve el HTML estándar para insertar en las plantillas.
     --------------------------------------------------------- */
  SPO.campoBuscador = function (opts) {
    return `
      <div class="buscador-campo input-group">
        <input type="hidden" id="${opts.idValor}" ${opts.extraAttr || ""} />
        <input type="text" id="${opts.idValor}Texto" class="form-control placeholder-muted"
               placeholder="${opts.placeholder}" readonly ${opts.required ? "required" : ""} />
        <button type="button" class="btn btn-outline-primary" onclick="${opts.onclick}" title="Buscar y seleccionar">
          <i class="bi bi-search"></i>
        </button>
        <button type="button" class="btn btn-outline-secondary" onclick="${opts.limpiar || ""}" title="Limpiar selección">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>`;
  };
})();
