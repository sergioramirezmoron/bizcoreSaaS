# 🏪 BizCore — SaaS de Gestión para Pequeñas y Medianas Empresas

> **v2.0** · Stack: Java 25 + Spring Boot 3.6 · React 19 + Tailwind v4 · PostgreSQL · Multi-tenant
>
> ⚠️ **Principios de diseño inamovibles**
> 1. Plataforma **100% web** — sin hardware, sin instalaciones, sin visitas físicas
> 2. Registro, pago y activación **completamente automáticos** — el operador no interviene
> 3. Cualquier problema se resuelve **en remoto** desde el panel de superadmin
> 4. Coste de infraestructura inicial **< 30 €/mes**, escalable sin rediseño de arquitectura

---

## Tabla de contenidos

0. [Análisis previo: viabilidad, legalidad y operación remota](#0-análisis-previo)
1. [Descripción del problema y la solución](#1-descripción-del-problema-y-la-solución)
2. [Análisis de mercado y necesidades reales](#2-análisis-de-mercado-y-necesidades-reales)
3. [Tipos de empresa y sectores soportados](#3-tipos-de-empresa-y-sectores-soportados)
4. [Roles y permisos](#4-roles-y-permisos)
5. [Códigos promocionales](#5-códigos-promocionales-temporales-y-permanentes)
6. [Arquitectura multi-tenant](#6-arquitectura-multi-tenant)
7. [Planes de suscripción](#7-planes-de-suscripción)
8. [Flujo de registro de empresas](#8-flujo-de-registro-de-empresas)
9. [Módulos y funcionalidades principales](#9-módulos-y-funcionalidades-principales)
10. [Panel de Superadmin](#10-panel-de-superadmin-operador-del-saas)
11. [Pasarela de pago con Stripe](#11-pasarela-de-pago-y-acceso-automático)
12. [Imágenes de productos](#12-imágenes-de-productos-upload-límites-y-coste)
13. [Stack tecnológico](#13-stack-tecnológico)
14. [Modelo de base de datos](#14-modelo-de-base-de-datos)
15. [Arquitectura de la aplicación](#15-arquitectura-de-la-aplicación)
16. [Seguridad](#16-seguridad)
17. [Funcionalidades extras opcionales](#17-funcionalidades-extras-opcionales)
18. [Escalabilidad sectorial](#18-expansión-y-escalabilidad-sectorial)
19. [¿Cuántas empresas necesito antes de lanzar?](#19-cuántas-empresas-necesito-antes-de-lanzar)
20. [Soporte remoto: cómo resolver cualquier problema sin visitas](#20-soporte-remoto-sin-visitas-físicas)
21. [Estrategia de venta y marketing](#21-estrategia-de-venta-y-marketing)
22. [Roadmap de desarrollo](#22-roadmap-de-desarrollo)
23. [Apéndice A — Dependencias Spring Initializr (backend)](#apéndice-a--dependencias-spring-initializr)
24. [Apéndice B — Setup del frontend (comandos npm)](#apéndice-b--setup-del-frontend-comandos-npm)
25. [Apéndice C — Variables de entorno](#apéndice-c--variables-de-entorno)

---

## 0. Análisis previo

### ¿Es técnicamente viable?

**SÍ, completamente.** Un SaaS multi-tenant con Java/Spring Boot + React es uno de los stacks más probados del mercado. No hay nada en este diseño que no esté resuelto con tecnología estándar. Existen miles de productos en producción con esta misma arquitectura. Los riesgos técnicos son bajos si se sigue el roadmap por fases.

Puntos de validación:

| Aspecto | Veredicto | Detalle |
|---|---|---|
| Multi-tenancy con RLS | ✅ Probado | PostgreSQL Row Level Security es estándar en SaaS |
| Pago automatizado sin intervención | ✅ Probado | Stripe Webhooks resuelve esto por completo |
| Sin hardware en el cliente | ✅ Es una ventaja | Es tu principal diferenciador frente a TPV físico |
| Subida de imágenes barata | ✅ Resuelto | Cloudflare R2: ~0 € para las primeras 10 GB |
| Java 25 + Spring Boot 3.6 | ✅ Compatible | Java 25 es LTS (sep 2025), Spring Boot 3.6 lo soporta |
| React 19 + Tailwind v4 | ✅ Maduro | Tailwind v4 GA desde enero 2025, shadcn/ui lo soporta |

### ¿Es legal en España y la UE?

**SÍ.** Ofrecer un SaaS de gestión empresarial es completamente legal. No necesitas ninguna licencia especial de software ni registro en ningún organismo técnico.

**Lo que SÍ debes tener en cuenta (obligaciones reales):**

| Obligación | Descripción | Coste/Esfuerzo |
|---|---|---|
| **Aviso legal** | Quién eres, datos de contacto, razón social | Una tarde redactando |
| **Términos y condiciones** | Condiciones del servicio, causas de suspensión, SLA | Una tarde + revisión legal |
| **Política de privacidad** | Qué datos recoges, cómo los usas, dónde se almacenan | Una tarde |
| **DPA (Data Processing Agreement)** | Contrato de encargado del tratamiento con cada empresa cliente. Los datos de sus empleados son datos personales bajo RGPD | Plantilla estándar, se acepta al registrarse |
| **Registro de actividades** | Registro interno de qué datos tratas (RGPD Art. 30) | Documento interno, no se publica |
| **Facturación con IVA** | Stripe puede generar facturas automáticas con IVA | Configuración en Stripe Tax |
| **Modelo 303 (IVA trimestral)** | Declarar trimestralmente el IVA cobrado | Gestor contable, ~50 €/trimestre |

> **Dato clave sobre el DPA**: Tus clientes (las PYMEs) te encargan tratar los datos personales de sus empleados (nombre, email, teléfono). Tú eres el "encargado del tratamiento". Debes ofrecer este contrato al registrarse. Una plantilla estándar es suficiente para empezar.

**Lo que NO necesitas:**
- Licencia de operador de telecomunicaciones
- Autorización de la AEPD (solo para ficheros de datos especialmente sensibles)
- Presencia física en ningún negocio
- Hardware homologado ni nada parecido

### ¿Tienes que ir físicamente a los negocios?

**NUNCA.** Esta es la premisa central del producto. El flujo completo es:

```
Cliente encuentra BizCore → Se registra solo en la web → Configura su empresa en el wizard
  → Prueba 14 días gratis → Añade tarjeta en Stripe Checkout → Pago automático
  → Webhook activa el acceso → El cliente está operativo
  → Si tiene un problema → Te escribe → Tú lo resuelves desde el panel de superadmin
  → Fin. Nunca has visitado su local.
```

Los únicos escenarios donde alguien podría pedirte ir en persona son:
- Un cliente que no sabe usar un navegador web (no es tu cliente objetivo)
- Un cliente que quiere una demo presencial de ventas (opcional, no técnico)

---

## 1. Descripción del problema y la solución

### El problema

La mayoría de las pequeñas y medianas empresas en España y Latinoamérica gestionan su negocio con herramientas fragmentadas: hojas de Excel para el stock, cuadernos para los pedidos, y la memoria del dueño para el resto. Este caos genera:

- **Pedidos mal registrados o perdidos**: sin trazabilidad de qué se pidió, quién lo atendió ni cuándo
- **Stock descontrolado**: no se sabe cuánto queda de cada producto hasta que ya se ha agotado
- **Falta de visibilidad financiera**: el propietario no sabe si su negocio es rentable hasta fin de mes
- **Dependencia de personas clave**: si falta un empleado, el conocimiento se va con él
- **Imposibilidad de escalar**: sin datos, no hay decisiones informadas

### La solución: BizCore

**BizCore** es un SaaS de gestión empresarial diseñado para PYMEs que funciona **íntegramente desde el navegador web**, sin instalar nada. Los empleados acceden con su cuenta y gestionan el negocio desde cualquier ordenador, tablet o móvil.

Funcionalidades centrales:

- Registro de pedidos por parte de los empleados desde la web
- Control de stock en tiempo real (se actualiza al registrar cada pedido)
- Control de ingresos y gastos
- Gestión de empleados con roles y permisos
- Reportes claros y descargables
- Alertas automáticas (stock bajo, gastos elevados, etc.)
- Acceso automático en cuanto la empresa paga: sin intervención manual del operador

**BizCore no es una herramienta genérica.** Cada tipo de empresa tiene un **perfil de sector** que adapta el vocabulario, los campos y los flujos a su realidad.

**BizCore no requiere hardware.** No hay lector de código de barras, no hay caja registradora, no hay impresora de tickets. Si el cliente tiene un navegador, puede usar BizCore.

---

## 2. Análisis de mercado y necesidades reales

| Área | Necesidades concretas |
|---|---|
| **Pedidos** | Registro manual de pedidos por empleados, historial, estado del pedido |
| **Productos** | CRUD de artículos, categorías, variantes (talla, color, sabor), precio de compra y venta, foto |
| **Stock** | Control de cantidades, alertas de mínimos, historial de movimientos |
| **Finanzas** | Ingresos (de los pedidos), gastos (alquiler, proveedores, sueldos), margen bruto |
| **Empleados** | Alta y baja, roles y permisos, registro de actividad |
| **Reportes** | Pedidos por período, productos más vendidos, rentabilidad por categoría |
| **Alertas** | Stock bajo, gastos inusuales, objetivos no cumplidos |
| **Proveedores** | Registro de proveedores, historial de compras |
| **Acceso remoto** | El dueño quiere ver el negocio desde casa o de vacaciones |

### ¿Por qué pagaría una empresa?

1. **Ahorro de tiempo**: registrar un pedido tarda 30 segundos en lugar de apuntarlo en papel
2. **Control financiero real**: el propietario sabe exactamente cuánto ingresa y cuánto gasta sin esperar al cierre mensual
3. **Prevención de pérdidas**: el sistema avisa antes de que el stock llegue a cero
4. **Decisiones basadas en datos**: ¿qué producto piden más? ¿cuál no vale la pena reponer?
5. **Tranquilidad**: si hay algún error o deshonestidad, la auditoría lo detecta
6. **Escalabilidad**: cuando el negocio crece, el sistema crece con él
7. **Acceso desde cualquier lugar**: no depende de un ordenador específico ni de estar en la tienda

### Funcionalidades que hacen un SaaS competitivo

- **Onboarding guiado** (wizard de configuración inicial en menos de 10 minutos)
- **Dashboard intuitivo** con KPIs visuales y sin jerga contable
- **Responsive design** para gestionar desde el móvil sin necesidad de app nativa
- **Exportación de datos** a Excel y PDF
- **Soporte multi-idioma** (español, inglés, portugués al menos)
- **API pública** para integraciones personalizadas (plan premium)

---

## 3. Tipos de empresa y sectores soportados

Una de las ventajas diferenciadoras de BizCore es el concepto de **Sector/Perfil de Negocio**. Cada empresa, al registrarse, elige su tipo de negocio. Esto configura automáticamente:

- El vocabulario de la interfaz ("producto" vs. "artículo" vs. "pan" vs. "pieza")
- Los campos activos en el formulario de producto
- Las categorías predefinidas por defecto
- Las alertas relevantes por defecto
- Los reportes y métricas específicos del sector

### Sectores soportados en v1.0

| Código | Sector | Ejemplos | Campos específicos |
|---|---|---|---|
| `RETAIL_FASHION` | Tienda de ropa y moda | Boutiques, tiendas de ropa, calzado | Talla, color, temporada, género |
| `BAKERY_FOOD` | Panadería y alimentación | Panaderías, pastelerías, colmados | Peso, alérgenos, caducidad, unidad (ud/kg) |
| `HARDWARE` | Ferretería y materiales | Ferreterías, fontanería, bricolaje | Referencia técnica, unidad (m, kg, ud) |
| `PHARMACY` | Farmacia y parafarmacia | Farmacias, herbolarios | Principio activo, código nacional |
| `BEAUTY` | Belleza y estética | Peluquerías, centros de estética | Tipo: servicio vs. producto |
| `GENERIC` | Comercio genérico | Cualquier negocio no clasificado | Campos básicos |

> `RESTAURANT_BAR` se pospone a v2.0 porque requiere lógica de comandas y mesas que añade complejidad sin aportar al MVP.

### Estructura en base de datos

```sql
CREATE TABLE business_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_key VARCHAR(50),
    default_category_template JSONB,
    product_fields_config JSONB,
    default_alerts_config JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### Ejemplo de `product_fields_config` para RETAIL_FASHION

```json
{
  "fields": [
    { "key": "size", "label": "Talla", "type": "select", "options": ["XS","S","M","L","XL","XXL"], "required": false },
    { "key": "color", "label": "Color", "type": "text", "required": false },
    { "key": "season", "label": "Temporada", "type": "select", "options": ["Primavera/Verano","Otoño/Invierno","Todo el año"], "required": false },
    { "key": "gender", "label": "Género", "type": "select", "options": ["Mujer","Hombre","Unisex","Niño"], "required": false },
    { "key": "material", "label": "Material/Composición", "type": "text", "required": false }
  ]
}
```

Esto permite que el formulario de producto se **genere dinámicamente** en el frontend según el sector, sin necesidad de crear múltiples módulos separados.

---

## 4. Roles y permisos

BizCore tiene dos capas de roles claramente separadas: los roles de **plataforma** (quién opera el SaaS) y los roles de **empresa** (quién trabaja en el negocio del cliente).

### Capa 1: Roles de plataforma

#### SUPER_ADMIN (tú, el operador del SaaS)

Accedes a `admin.bizcore.app` con tu cuenta SUPER_ADMIN. Nadie más tiene acceso a esta URL.

Capacidades completas:

- Ver todas las empresas registradas, su plan y estado de suscripción
- Ver métricas globales: MRR, empresas activas, churn, trials, conversiones
- Buscar una empresa por nombre, email o NIF
- Ver el detalle completo de cualquier empresa (modo lectura para soporte)
- Impersonar a un usuario de una empresa para reproducir problemas de soporte
- Aplicar descuentos manuales o cupones a una empresa concreta
- Regalar meses de suscripción con un clic
- Cambiar el plan de una empresa manualmente
- Suspender o reactivar una empresa
- Ver el historial de pagos y facturas de cada empresa
- Enviar emails manuales a una empresa desde el panel
- Gestionar los `BusinessType` del sistema (añadir nuevos sectores sin código)
- Crear y gestionar códigos promocionales (temporales, permanentes, de uso único)
- Ver logs de errores y actividad crítica de la plataforma

### Capa 2: Roles de empresa (usuarios del cliente)

#### OWNER (Propietario de la empresa)

- Configura la empresa (nombre, sector, logo, datos fiscales)
- Gestiona todos los empleados y asigna roles
- Ve todos los reportes y analíticas
- Gestiona la suscripción desde el portal de cliente de Stripe
- Configura alertas y notificaciones
- Puede transferir la propiedad a otro usuario
- Crea y gestiona productos, categorías y precios
- Ve el panel financiero completo (ingresos, gastos, margen)

#### ADMIN (Administrador / Encargado de confianza)

- CRUD completo de productos, categorías y proveedores
- Gestión de empleados (sin poder crear OWNER ni otros ADMIN)
- Acceso a reportes completos
- Gestión de stock: entradas manuales, correcciones, inventario
- Configuración de precios y descuentos en pedidos
- No puede gestionar la suscripción ni datos de facturación

#### MANAGER (Encargado de turno)

- Registro y gestión de pedidos del día
- Revisión de pedidos de la semana
- Correcciones simples de stock
- Vista de alertas activas
- No puede modificar precios ni crear/eliminar productos

#### EMPLOYEE (Empleado)

- Registro de nuevos pedidos
- Consulta de stock disponible
- Consulta de productos y precios
- Ver sus propios pedidos registrados
- Sin acceso a reportes financieros ni datos de otros empleados

#### VIEWER (Solo lectura / Socio / Inversor)

- Vista de dashboard con KPIs
- Vista de reportes (solo lectura)
- Sin capacidad de modificar nada

### Tabla de permisos

| Acción | SUPER_ADMIN | OWNER | ADMIN | MANAGER | EMPLOYEE | VIEWER |
|---|:-:|:-:|:-:|:-:|:-:|:-:|
| Ver dashboard empresa | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Registrar pedido | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| Editar/cancelar pedido | ✅ | ✅ | ✅ | ✅ | Solo propio | ❌ |
| CRUD productos | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Subir fotos de producto | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Modificar precios | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Ajuste de stock | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ |
| Ver reportes | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| Exportar reportes | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Gestionar empleados | ✅ | ✅ | Parcial | ❌ | ❌ | ❌ |
| Ver finanzas | ✅ | ✅ | ✅ | Parcial | ❌ | ✅ |
| Gestionar suscripción | Solo SA | ✅ | ❌ | ❌ | ❌ | ❌ |
| Gestionar sucursales | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Panel superadmin | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

---

## 5. Códigos promocionales (temporales y permanentes)

Los códigos promocionales permiten atraer nuevos clientes con incentivos sin tocar la configuración de Stripe manualmente. El SUPER_ADMIN los crea desde el panel, y los clientes los introducen al registrarse.

### Tipos de código promocional

| Tipo | Efecto | Caso de uso |
|---|---|---|
| `TRIAL_EXTENSION` | Extiende el período de prueba X días adicionales | "Prueba 30 días en lugar de 14" |
| `PERCENT_DISCOUNT` | Descuento % durante N meses al suscribirse | "20% off los primeros 3 meses" |
| `FREE_MONTHS` | N meses gratuitos tras suscribirse | "2 meses gratis por referido" |
| `PLAN_UPGRADE` | Acceso a un plan superior al precio del inferior | "Standard al precio de Basic" |

### Configuraciones posibles

| Parámetro | Descripción | Ejemplo |
|---|---|---|
| **Temporal** | Código con fecha de expiración | `BLACKFRIDAY25` válido hasta el 30/11/2025 |
| **Permanente** | Sin fecha de expiración | Para socios o canales de distribución |
| **Uso único** | Solo puede usarlo una empresa | Para onboarding individual |
| **Usos limitados** | Máximo N empresas | Campaña limitada a 100 usos |
| **Ilimitado** | Sin límite de usos | Para campañas de largo plazo |

### Modelo de datos

```sql
CREATE TABLE promo_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,              -- 'LANZAMIENTO25', 'FERRETERO30', 'VIP2025'
    description TEXT,                              -- nota interna del superadmin
    type VARCHAR(30) NOT NULL,                     -- TRIAL_EXTENSION | PERCENT_DISCOUNT | FREE_MONTHS | PLAN_UPGRADE
    value JSONB NOT NULL,
    -- TRIAL_EXTENSION:  {"days": 30}
    -- PERCENT_DISCOUNT: {"percent": 20, "duration_months": 3}
    -- FREE_MONTHS:      {"months": 2}
    -- PLAN_UPGRADE:     {"plan": "STANDARD", "price_of_plan": "BASIC"}
    applicable_plans VARCHAR(20)[],               -- planes en los que aplica (null = todos)
    max_uses INT,                                  -- null = ilimitado
    current_uses INT DEFAULT 0,
    valid_from TIMESTAMP DEFAULT NOW(),
    valid_until TIMESTAMP,                         -- null = permanente
    is_active BOOLEAN DEFAULT TRUE,
    stripe_coupon_id VARCHAR(100),                 -- ID del cupón creado en Stripe (para descuentos monetarios)
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE promo_code_usages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promo_code_id UUID NOT NULL REFERENCES promo_codes(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    applied_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(promo_code_id, company_id)              -- cada empresa solo puede usar el mismo código una vez
);
```

### Flujo de aplicación durante el registro

```
1. Empresa rellena el formulario de registro
2. Campo opcional: "¿Tienes un código promocional?" [__________]
3. Al hacer clic en "Verificar código" → POST /api/v1/promo-codes/validate
4. Backend valida:
   - Que el código existe y está activo
   - Que no ha expirado (valid_until > NOW())
   - Que no ha alcanzado el máximo de usos
   - Que la empresa no lo ha usado antes (check en promo_code_usages)
5. Si válido → devuelve descripción del beneficio al frontend (ej: "¡30 días de prueba gratis!")
6. Al completar el registro → se aplica automáticamente:
   - TRIAL_EXTENSION: trial_ends_at += días adicionales
   - PERCENT_DISCOUNT: se crea/aplica coupon de Stripe al hacer checkout
   - FREE_MONTHS: se añade crédito en Stripe CustomerBalance
   - PLAN_UPGRADE: se activa el plan superior cuando el usuario pague
7. Se registra el uso en promo_code_usages
```

### Endpoint de validación

```java
@RestController
@RequestMapping("/api/v1/promo-codes")
public class PromoCodeController {

    @PostMapping("/validate")
    public ResponseEntity<PromoCodeValidationResponse> validate(
        @RequestBody @Valid PromoCodeValidationRequest req
    ) {
        PromoCode code = promoCodeService.validate(req.getCode());
        return ResponseEntity.ok(new PromoCodeValidationResponse(
            true,
            code.getType(),
            code.getHumanReadableBenefit(),  // "30 días de prueba adicionales"
            code.getValue()
        ));
    }
}
```

### Gestión desde el panel de superadmin

El SUPER_ADMIN puede desde el panel:
- Crear nuevos códigos (formulario con todos los parámetros)
- Ver listado de códigos con usos actuales / máximo
- Desactivar un código en cualquier momento (acceso inmediato)
- Ver qué empresas han usado cada código
- Editar la fecha de expiración de un código activo

---

## 6. Arquitectura multi-tenant

BizCore utiliza un modelo **multi-tenant con aislamiento por `tenant_id`** en la misma base de datos (schema compartido con Row Level Security). Esta estrategia es la más eficiente y segura para escalar a miles de empresas manteniendo un único despliegue.

### Estrategia: Row-Level Security (RLS) en PostgreSQL

Todas las tablas de datos de negocio incluyen `tenant_id`. Las políticas RLS garantizan que ningún tenant pueda ver datos de otro, incluso si hay un error en la capa de aplicación.

```sql
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation ON orders
    USING (tenant_id = current_setting('app.current_tenant')::UUID);
```

El `tenant_id` se extrae del JWT en cada request y se establece en la sesión de PostgreSQL antes de cualquier consulta.

### Entidad central: `Company` (Tenant)

```sql
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    business_type_id UUID REFERENCES business_types(id),
    tax_id VARCHAR(50),
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(150) NOT NULL,
    logo_url TEXT,
    timezone VARCHAR(50) DEFAULT 'Europe/Madrid',
    currency VARCHAR(10) DEFAULT 'EUR',
    locale VARCHAR(10) DEFAULT 'es-ES',

    -- Suscripción
    subscription_plan VARCHAR(20) DEFAULT 'TRIAL',    -- TRIAL | BASIC | STANDARD | PREMIUM
    subscription_status VARCHAR(20) DEFAULT 'TRIAL',  -- TRIAL | ACTIVE | PAST_DUE | CANCELLED | SUSPENDED
    trial_ends_at TIMESTAMP,
    plan_override_by_admin BOOLEAN DEFAULT FALSE,
    admin_notes TEXT,

    -- Stripe
    stripe_customer_id VARCHAR(100),
    stripe_subscription_id VARCHAR(100),

    -- Código promo aplicado en el registro
    promo_code_id UUID REFERENCES promo_codes(id),

    -- Límites según plan
    max_employees INT DEFAULT 3,
    max_branches INT DEFAULT 1,
    max_products INT DEFAULT 100,
    max_product_images INT DEFAULT 1,

    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### Estructura de URLs

Todos los clientes acceden por la misma URL. El tenant se identifica exclusivamente por el **JWT**, que ya contiene el `tenant_id` en cada request.

```
bizcore.app           → Landing page (marketing)
app.bizcore.app       → App de todas las empresas (React)
admin.bizcore.app     → Panel de superadmin (protegido por rol SUPER_ADMIN)
```

> No hay subdominios por empresa. Esto evita el coste de planes Pro en Vercel/Netlify (~20 €/mes) que son necesarios para soportar wildcards. El `TenantFilter` de Spring extrae el `tenant_id` del JWT, no del header `Host`.

---

## 7. Planes de suscripción

### Tabla comparativa

| Funcionalidad | Basic (9€/mes) | Standard (19€/mes) | Premium (39€/mes) |
|---|:-:|:-:|:-:|
| Productos (máx.) | 100 | 1.000 | Ilimitados |
| Empleados (máx.) | 3 | 10 | Ilimitados |
| Sucursales | 1 | 2 | 5 |
| Fotos por producto | 1 (máx. 1 MB) | 3 (máx. 2 MB) | 5 (máx. 5 MB) |
| Registro de pedidos | ✅ | ✅ | ✅ |
| Control de stock | ✅ | ✅ | ✅ |
| Historial de pedidos | 30 días | 1 año | Ilimitado |
| Gestión de gastos | ❌ | ✅ | ✅ |
| Reportes básicos | ✅ | ✅ | ✅ |
| Exportar a Excel/PDF | ❌ | ✅ | ✅ |
| Alertas básicas (email) | ✅ | ✅ | ✅ |
| Alertas por WhatsApp | ❌ | ❌ | ✅ |
| Historial de auditoría | 7 días | 90 días | 1 año |
| Gestión de proveedores | ❌ | ✅ | ✅ |
| CRM básico de clientes | ❌ | ❌ | ✅ |
| Reportes por categoría | ❌ | ✅ | ✅ |
| API pública | ❌ | ❌ | ✅ |
| Soporte | Email (48h) | Email (24h) | Chat prioritario (4h) |
| Prueba gratuita | 14 días sin tarjeta | 14 días sin tarjeta | 14 días sin tarjeta |

> **Pago anual**: 20% de descuento para reducir churn y mejorar el flujo de caja. Configurado automáticamente como precio adicional en Stripe.

### Beneficios manuales desde el panel de superadmin

Además del pricing estándar, el SUPER_ADMIN puede aplicar beneficios sin tocar Stripe directamente:

- **Meses gratis**: crédito en `stripe.CustomerBalance`
- **Descuento porcentual**: `coupon` de Stripe aplicado a la suscripción
- **Upgrade de plan gratuito**: sube el plan marcando `plan_override_by_admin = true`
- **Trial extendido**: amplía el período de prueba antes de que expire

Todo queda registrado en `admin_actions` para trazabilidad completa.

```sql
CREATE TABLE admin_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    action_type VARCHAR(50) NOT NULL,
    -- FREE_MONTHS | PERCENT_DISCOUNT | PLAN_OVERRIDE | TRIAL_EXTEND | SUSPEND | REACTIVATE | NOTE | PROMO_CODE_MANUAL
    description TEXT NOT NULL,
    value JSONB,
    performed_by UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

## 8. Flujo de registro de empresas

El registro es completamente self-service. El operador (tú) no interviene en ningún momento.

### Paso a paso del wizard de registro

```
Paso 1 — Datos del propietario
  • Nombre y apellidos
  • Email (será el login principal)
  • Contraseña
  • Teléfono (opcional)

Paso 2 — Datos de la empresa
  • Nombre del negocio
  • NIF/CIF (opcional, para facturación)
  • Dirección (opcional)
  • Teléfono del negocio (opcional)

Paso 3 — Tipo de negocio
  • Selector visual con iconos: Tienda de ropa, Panadería, Ferretería, Farmacia, Estética, Otro
  • Esta elección configura automáticamente el vocabulario, campos y alertas

Paso 4 — Código promocional (opcional)
  • Campo: "¿Tienes un código promocional?" con botón de verificación
  • Si válido: muestra el beneficio ("¡14 días adicionales de prueba!")
  • Si inválido: mensaje de error claro

Paso 5 — Confirmación y acceso
  • Resumen de lo elegido
  • Aceptar Términos de Uso y Política de Privacidad + DPA (checkbox obligatorio)
  • Clic en "Crear mi cuenta gratis"
  • → Email de bienvenida automático
  • → Acceso inmediato al dashboard (período de prueba activo)
```

### Lo que ocurre automáticamente en el backend al registrar

```java
@Transactional
public CompanyRegistrationResult register(CompanyRegistrationCommand cmd) {
    // 1. Crear el usuario OWNER
    User owner = userService.createOwner(cmd.getOwnerData());

    // 2. Crear la empresa (tenant)
    Company company = companyService.create(cmd.getCompanyData(), owner);

    // 3. Aplicar código promo si existe
    if (cmd.getPromoCode() != null) {
        promoCodeService.applyToCompany(cmd.getPromoCode(), company);
    }

    // 4. Crear el cliente en Stripe (sin cobrar nada todavía)
    String stripeCustomerId = stripeService.createCustomer(company, owner);
    company.setStripeCustomerId(stripeCustomerId);

    // 5. Configurar el tenant: categorías, alertas y campos por defecto según sector
    businessTypeService.bootstrapTenant(company);

    // 6. Email de bienvenida automático
    emailService.sendWelcome(owner, company);

    // 7. Registrar en audit_logs
    auditService.log("COMPANY_REGISTERED", company.getId(), owner.getId());

    return new CompanyRegistrationResult(company, owner, generateJwtTokens(owner));
}
```

### Restricciones de acceso según estado de suscripción

| Estado | Acceso |
|---|---|
| `TRIAL` (activo) | Acceso completo a todas las funcionalidades del plan STANDARD durante el trial |
| `TRIAL` (expirado sin pagar) | Dashboard de solo lectura + banner para suscribirse |
| `ACTIVE` | Acceso completo según su plan |
| `PAST_DUE` | Acceso en modo lectura + banner urgente de pago |
| `CANCELLED` | Solo exportación de datos durante 30 días, luego acceso bloqueado |
| `SUSPENDED` | Acceso bloqueado con mensaje del superadmin |

---

## 9. Módulos y funcionalidades principales

### 9.1 Registro de pedidos (sin TPV, sin hardware)

Este es el módulo central. Un empleado entra a la web, abre el formulario de nuevo pedido, selecciona los productos y las cantidades, y guarda. El stock se descuenta automáticamente.

**No hay TPV, no hay escáner de código de barras, no hay impresora de tickets, no hay caja registradora.** Todo es un formulario web accesible desde cualquier navegador.

#### Flujo de un pedido

```
1. El empleado accede a su cuenta en app.bizcore.app
2. Hace clic en "Nuevo pedido"
3. Busca productos por nombre o referencia (buscador con autocompletado)
4. Añade líneas: producto + cantidad + precio unitario (prellenado, editable si tiene permiso)
5. Selecciona método de pago: Efectivo / Tarjeta / Transferencia / Pendiente
6. Añade notas opcionales
7. Guarda el pedido → stock se descuenta automáticamente
8. El pedido queda en el historial con el nombre del empleado que lo registró
```

#### Estados de un pedido

```
PENDING    → Registrado pero pendiente de cobro
COMPLETED  → Cobrado y cerrado
CANCELLED  → Cancelado (el stock se restaura automáticamente)
REFUNDED   → Devuelto parcial o totalmente
```

#### Modelo de datos

```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    branch_id UUID REFERENCES branches(id),
    order_number VARCHAR(30) NOT NULL,             -- auto-generado: ORD-2025-00342
    employee_id UUID NOT NULL REFERENCES users(id),
    customer_id UUID REFERENCES customers(id),     -- opcional, solo plan Premium
    status VARCHAR(20) DEFAULT 'PENDING',
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30),                    -- CASH | CARD | TRANSFER | PENDING | OTHER
    notes TEXT,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    product_id UUID REFERENCES products(id),
    variant_id UUID REFERENCES product_variants(id),
    product_name VARCHAR(200) NOT NULL,            -- snapshot del nombre en el momento del pedido
    quantity DECIMAL(10,3) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0,
    line_total DECIMAL(10,2) NOT NULL
);

CREATE TABLE refunds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    order_id UUID NOT NULL REFERENCES orders(id),
    employee_id UUID REFERENCES users(id),
    refund_amount DECIMAL(10,2) NOT NULL,
    reason TEXT,
    refund_type VARCHAR(20),                       -- FULL | PARTIAL
    refund_method VARCHAR(30),
    created_at TIMESTAMP DEFAULT NOW()
);
```

Al cancelar o devolver un pedido, el stock se **restaura automáticamente** mediante un evento de dominio.

---

### 9.2 Gestión de productos y categorías

```sql
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    parent_id UUID REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    category_id UUID REFERENCES categories(id),
    sku VARCHAR(100),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    purchase_price DECIMAL(10,2),
    selling_price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 21.00,
    unit VARCHAR(20) DEFAULT 'ud',
    custom_fields JSONB,                            -- campos dinámicos según sector
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Imágenes de producto (ver sección 12 para el flujo de upload)
CREATE TABLE product_images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    image_url TEXT NOT NULL,                        -- URL pública en Cloudflare R2
    r2_key TEXT NOT NULL,                           -- clave en R2 para borrado futuro
    size_bytes INT NOT NULL,
    sort_order INT DEFAULT 0,                       -- 0 = imagen principal
    uploaded_at TIMESTAMP DEFAULT NOW()
);
```

#### Variantes de producto

```sql
CREATE TABLE product_variants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    sku VARCHAR(100),
    variant_name VARCHAR(100),                     -- "Azul / M"
    attributes JSONB,                              -- {"color": "Azul", "size": "M"}
    selling_price DECIMAL(10,2),
    is_active BOOLEAN DEFAULT TRUE
);
```

---

### 9.3 Gestión de stock e inventario

```sql
CREATE TABLE stock_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    product_id UUID REFERENCES products(id),
    variant_id UUID REFERENCES product_variants(id),
    branch_id UUID REFERENCES branches(id),
    quantity DECIMAL(10,3) NOT NULL DEFAULT 0,
    min_quantity DECIMAL(10,3) DEFAULT 0,
    max_quantity DECIMAL(10,3),
    location VARCHAR(100),
    last_updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    stock_item_id UUID NOT NULL REFERENCES stock_items(id),
    movement_type VARCHAR(30) NOT NULL,            -- ORDER | PURCHASE | ADJUSTMENT | RETURN | TRANSFER
    quantity_change DECIMAL(10,3) NOT NULL,
    quantity_before DECIMAL(10,3) NOT NULL,
    quantity_after DECIMAL(10,3) NOT NULL,
    reference_id UUID,
    notes TEXT,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Reglas de negocio del stock:**

- Al registrar un pedido → stock se descuenta automáticamente (evento de dominio)
- Al cancelar/devolver un pedido → stock se restaura automáticamente
- Al registrar una compra a proveedor → stock aumenta
- Si stock cae al `min_quantity` → se genera una alerta
- Ajustes manuales requieren justificación y quedan en el historial

---

### 9.4 Gestión de ingresos y gastos

```sql
CREATE TABLE expense_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7),
    is_system BOOLEAN DEFAULT FALSE
);

CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    branch_id UUID REFERENCES branches(id),
    category_id UUID REFERENCES expense_categories(id),
    supplier_id UUID REFERENCES suppliers(id),
    amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    description TEXT NOT NULL,
    expense_date DATE NOT NULL,
    payment_method VARCHAR(30),
    receipt_url TEXT,                              -- foto del ticket subida (mismo sistema R2)
    status VARCHAR(20) DEFAULT 'PAID',
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Dashboard financiero:**

- Ingresos del día / semana / mes comparados con el período anterior
- Gastos del mes desglosados por categoría
- Beneficio bruto = Ingresos - Coste de mercancía vendida
- Margen bruto en porcentaje
- Alerta si los gastos superan el X% de los ingresos (configurable)
- Gráfico de tendencia de los últimos 30 días

---

### 9.5 Gestión de empleados

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID,                               -- null = SUPER_ADMIN de plataforma
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    avatar_url TEXT,
    role VARCHAR(30) NOT NULL,                    -- OWNER | ADMIN | MANAGER | EMPLOYEE | VIEWER | SUPER_ADMIN
    branch_id UUID REFERENCES branches(id),
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

- Alta de empleados con envío de email de invitación (el empleado crea su contraseña)
- Asignación de rol con descripción clara de qué puede hacer
- Asignación de sucursal (plan Standard+)
- Historial de actividad: pedidos registrados, ajustes de stock, etc.
- Desactivación en lugar de eliminación (conserva el historial)

---

### 9.6 Reportes y analíticas

| Reporte | Plan mínimo | Descripción |
|---|---|---|
| Pedidos del día | Basic | Resumen de pedidos de hoy |
| Pedidos por período | Basic | Filtrar por rango de fechas |
| Productos más pedidos | Basic | Top 10 por cantidad |
| Stock actual | Basic | Lista con cantidades |
| Pedidos por empleado | Standard | Quién registra más |
| Pedidos por categoría | Standard | Qué categoría genera más ingresos |
| Rentabilidad por producto | Standard | Margen por producto |
| Gastos por categoría | Standard | Desglose de gastos |
| P&L mensual | Standard | Pérdidas y ganancias |
| Tendencias históricas | Premium | Comparativa entre períodos |
| Reporte de auditoría | Premium | Todos los cambios del sistema |
| Exportación Excel/PDF | Standard | Descargar cualquier reporte |

El backend genera archivos con **Apache POI** (Excel) e **iText** (PDF).

---

### 9.7 Alertas y notificaciones

| Alerta | Disparador | Canal | Plan |
|---|---|---|---|
| Stock bajo | `quantity <= min_quantity` | Email, dashboard | Basic |
| Stock agotado | `quantity == 0` | Email, dashboard | Basic |
| Gasto elevado | Gasto único > umbral configurable | Email | Standard |
| Objetivo diario no alcanzado | Fin del día sin cumplir objetivo | Email | Standard |
| Alerta de seguridad | Login desde nuevo dispositivo | Email | Basic |
| Resumen diario automático | A hora configurable | Email | Standard |
| Alertas por WhatsApp | Cualquiera de las anteriores | WhatsApp | Premium |

```sql
CREATE TABLE alert_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    threshold_value DECIMAL(10,2),
    notify_email BOOLEAN DEFAULT TRUE,
    notify_whatsapp BOOLEAN DEFAULT FALSE,
    notify_roles VARCHAR[],
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE alert_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    reference_id UUID,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT NOW()
);
```

---

### 9.8 Sucursales múltiples

```sql
CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    is_main BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);
```

- Stock gestionado por sucursal (independiente o consolidado en reportes)
- Pedidos asociados a una sucursal
- Reportes filtrables por sucursal o en vista consolidada
- Transferencias de stock entre sucursales con historial de movimientos

---

### 9.9 Auditoría y trazabilidad

```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    user_id UUID REFERENCES users(id),
    action VARCHAR(50) NOT NULL,                  -- CREATE, UPDATE, DELETE, LOGIN, etc.
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
```

Implementada con **AOP en Spring** para no contaminar la lógica de negocio:

```java
@Aspect
@Component
public class AuditAspect {
    @AfterReturning(pointcut = "@annotation(Auditable)", returning = "result")
    public void logAudit(JoinPoint jp, Object result) {
        // construir y persistir el AuditLog automáticamente
    }
}

@Auditable(action = "CREATE", entity = "Order")
public Order createOrder(CreateOrderCommand cmd) { ... }
```

Retención: 7 días (Basic), 90 días (Standard), 1 año (Premium).

---

## 10. Panel de Superadmin (operador del SaaS)

Panel privado accesible en `admin.bizcore.app`. Solo el SUPER_ADMIN tiene acceso.

### Dashboard global

Métricas en tiempo real:

- **MRR** con gráfico de evolución mensual
- **Empresas activas** / en trial / suspendidas / canceladas
- **Nuevas altas** de los últimos 30 días
- **Churn del mes**: cuántas empresas han cancelado
- **Conversión trial → paid**: porcentaje global y del último mes
- **Distribución por plan** y **por sector**
- **Alertas**: empresas con `PAST_DUE` que requieren atención

### Gestión de empresas

Lista paginada con filtros de todas las empresas. Vista detalle incluye:

- Datos de la empresa y del propietario
- Estado de suscripción e historial de pagos
- Número de empleados, productos y pedidos registrados
- Logs de actividad reciente
- Notas privadas (`admin_notes`)

### Acciones manuales (sin tocar código ni Stripe directamente)

```
[Regalar meses gratis]    → input: número de meses + motivo
[Aplicar descuento]       → input: porcentaje + duración + motivo
[Cambiar plan]            → selector de plan + motivo
[Extender trial]          → input: días adicionales
[Suspender empresa]       → con mensaje de aviso al owner
[Reactivar empresa]       → acceso inmediato
[Enviar email manual]     → asunto + cuerpo desde el panel
[Añadir nota interna]     → visible solo para el superadmin
[Impersonar usuario]      → ver la app como el cliente la ve (para soporte)
```

### Gestión de códigos promocionales

Desde el panel puedes:

- Crear nuevos códigos (todos los tipos)
- Ver listado con usos actuales vs. máximo permitido
- Desactivar códigos al instante
- Ver qué empresas han usado cada código y cuándo

### Finanzas del SaaS

- MRR desglosado por plan
- ARR estimado
- Histórico de ingresos mes a mes
- Lista de últimos cobros y fallos de pago

### Implementación técnica

```java
@RestController
@RequestMapping("/admin/api/v1")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    @GetMapping("/dashboard")
    public PlatformDashboardDTO getDashboard() { ... }

    @GetMapping("/companies")
    public Page<CompanySummaryDTO> listCompanies(Pageable pageable, CompanyFilter filter) { ... }

    @PostMapping("/companies/{id}/free-months")
    public void grantFreeMonths(@PathVariable UUID id, @RequestBody FreeMonthsRequest req) {
        // 1. Aplicar crédito en Stripe (CustomerBalance)
        // 2. Registrar en admin_actions
        // 3. Email de notificación al owner (opcional)
    }

    @PostMapping("/companies/{id}/impersonate")
    public ImpersonationTokenResponse impersonate(
        @PathVariable UUID id,
        @RequestBody ImpersonateRequest req  // qué usuario impersonar
    ) {
        // Genera un JWT temporal con flag impersonation=true y el tenant del cliente
        // Queda registrado en audit_logs con el ID del superadmin
    }

    @GetMapping("/promo-codes")
    public Page<PromoCodeDTO> listPromoCodes(Pageable pageable) { ... }

    @PostMapping("/promo-codes")
    public PromoCodeDTO createPromoCode(@RequestBody @Valid CreatePromoCodeRequest req) { ... }

    @DeleteMapping("/promo-codes/{id}")
    public void deactivatePromoCode(@PathVariable UUID id) { ... }
}
```

---

## 11. Pasarela de pago y acceso automático

### Plataforma: Stripe

Stripe es el estándar para SaaS modernos. Razones:

- Pagos recurrentes (subscriptions) nativos
- **Stripe Customer Portal**: los clientes gestionan su suscripción solos sin que tengas que implementarlo
- **Stripe Tax**: calcula IVA automáticamente según el país del cliente
- Webhooks fiables para reaccionar a eventos de pago en tiempo real
- Dashboard propio para ver todos los ingresos del SaaS
- **Stripe Billing**: genera facturas PDF automáticas para tus clientes
- Comisión: 1.4% + 0.25 € (tarjetas europeas). Sin cuota mensual.

### Flujo completo sin intervención del operador

```
1.  Empresa se registra → Company(status=TRIAL, trial_ends_at = NOW() + 14 días)
        → Customer creado en Stripe (sin cobrar)
        → Email de bienvenida automático
        → Acceso inmediato

2.  Día 10: Email automático "Tu prueba termina en 4 días"
    Día 13: Email final "Mañana termina tu prueba gratuita"

3.  Usuario elige plan → clic en "Suscribirme"
        → POST /api/v1/billing/checkout
        → Backend crea Stripe Checkout Session (página de pago de Stripe)
        → Si tiene código promo PERCENT_DISCOUNT → se aplica el coupon de Stripe

4.  Usuario completa el pago en la página de Stripe
        → Stripe redirige a app.bizcore.app/billing/success

5.  Backend recibe webhook invoice.paid
        → subscription_status = ACTIVE
        → Límites actualizados según plan
        → Email de confirmación al owner
        → Acceso activo inmediatamente

6.  Renovación mensual automática
        → Stripe cobra → webhook → confirmación automática

7.  Fallo de pago
        → invoice.payment_failed → status = PAST_DUE
        → Email automático al owner
        → Stripe reintenta 3 veces en 7 días
        → Si todos fallan → status = CANCELLED → acceso restringido

8.  Cancelación (cliente desde Stripe Customer Portal)
        → customer.subscription.deleted
        → Acceso hasta fin del período pagado → luego CANCELLED
```

### Integración Spring Boot

```java
@RestController
@RequestMapping("/api/v1/billing")
public class BillingController {

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createCheckout(@RequestBody CheckoutRequest req) {
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setSuccessUrl("https://app.bizcore.app/facturacion/exito?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("https://app.bizcore.app/planes")
            .addLineItem(SessionCreateParams.LineItem.builder()
                .setPrice(getPriceId(req.getPlan()))
                .setQuantity(1L)
                .build())
            .setCustomer(company.getStripeCustomerId())
            .setTrialEnd(company.getTrialEndsAt().toEpochSecond());

        // Aplicar cupón si la empresa tiene un código promo de tipo PERCENT_DISCOUNT
        if (company.getPromoCouponId() != null) {
            builder.addDiscount(SessionCreateParams.Discount.builder()
                .setCoupon(company.getPromoCouponId())
                .build());
        }

        Session session = Session.create(builder.build());
        return ResponseEntity.ok(new CheckoutResponse(session.getUrl()));
    }

    @PostMapping("/portal")
    public ResponseEntity<PortalResponse> createPortal() {
        com.stripe.model.billingportal.Session session =
            com.stripe.model.billingportal.Session.create(
                SessionCreateParams.builder()
                    .setCustomer(company.getStripeCustomerId())
                    .setReturnUrl("https://app.bizcore.app/configuracion/facturacion")
                    .build()
            );
        return ResponseEntity.ok(new PortalResponse(session.getUrl()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String signature
    ) {
        Event event = Webhook.constructEvent(payload, signature, webhookSecret);
        switch (event.getType()) {
            case "invoice.paid"                  -> subscriptionService.activate(event);
            case "invoice.payment_failed"        -> subscriptionService.markPastDue(event);
            case "customer.subscription.deleted" -> subscriptionService.cancel(event);
            case "customer.subscription.updated" -> subscriptionService.updatePlan(event);
        }
        return ResponseEntity.ok().build();
    }
}
```

---

## 12. Imágenes de productos (upload, límites y coste)

### Estrategia: upload directo a Cloudflare R2

El frontend no sube imágenes a través del backend. El backend genera una **URL pre-firmada** y el frontend sube directamente a R2. Esto elimina carga de red en el servidor.

### Límites por plan

| Plan | Imágenes por producto | Tamaño máximo | Formatos |
|---|:-:|:-:|---|
| Basic | 1 | 1 MB | JPEG, PNG |
| Standard | 3 | 2 MB | JPEG, PNG, WebP |
| Premium | 5 | 5 MB | JPEG, PNG, WebP, AVIF |

### Flujo completo de upload

```
1. Usuario selecciona imagen en el formulario
2. Frontend comprime automáticamente (browser-image-compression):
   - Redimensiona a máximo 1200×1200 px
   - Comprime manteniendo calidad aceptable
   - Valida formato y tamaño ANTES de llamar al backend
3. Frontend llama: GET /api/v1/products/{id}/upload-url?contentType=image/jpeg
4. Backend valida:
   - ¿El tenant puede subir más imágenes según su plan?
   - ¿El contentType es permitido?
   → Genera URL pre-firmada de R2 válida por 5 minutos
5. Frontend hace PUT directamente a la URL pre-firmada (sin pasar por el backend)
6. Al guardar el producto → frontend envía el image_url definitivo
7. Backend guarda en product_images (url pública + r2_key para futuro borrado)
```

### Endpoint de pre-signed URL

```java
@GetMapping("/api/v1/products/{productId}/upload-url")
@PreAuthorize("hasPermission('PRODUCT_WRITE')")
public ResponseEntity<UploadUrlResponse> getUploadUrl(
    @PathVariable UUID productId,
    @RequestParam String contentType,
    @RequestParam long fileSizeBytes
) {
    // Validar límites del plan
    int currentImages = productImageRepository.countByProductId(productId);
    int maxImages = planLimitsService.getMaxProductImages(tenantId);
    if (currentImages >= maxImages) {
        throw new PlanLimitExceededException("Tu plan permite máximo " + maxImages + " imágenes por producto");
    }

    long maxBytes = planLimitsService.getMaxImageSizeBytes(tenantId);
    if (fileSizeBytes > maxBytes) {
        throw new FileTooLargeException("El archivo supera el límite de " + (maxBytes / 1024 / 1024) + " MB");
    }

    // Generar clave única en R2
    String r2Key = "tenants/" + tenantId + "/products/" + productId + "/" + UUID.randomUUID() + getExtension(contentType);

    // Generar pre-signed URL (válida 5 minutos)
    String presignedUrl = r2Service.generatePresignedUploadUrl(r2Key, contentType, Duration.ofMinutes(5));
    String publicUrl = "https://cdn.bizcore.app/" + r2Key;

    return ResponseEntity.ok(new UploadUrlResponse(presignedUrl, publicUrl, r2Key));
}
```

### Coste real de almacenamiento

Cloudflare R2 no cobra por egress (salida de datos) cuando se sirve vía Cloudflare CDN.

| Escenario | Almacenamiento | Coste mensual |
|---|---|---|
| 50 empresas × 100 productos × 1 imagen × 500 KB | 2.5 GB | ~0.04 € |
| 200 empresas × 200 productos × 2 imágenes × 1 MB | 80 GB | ~1.20 € |
| 1000 empresas × 500 productos × 3 imágenes × 1 MB | 1.5 TB | ~22.50 € |

> Con 1000 empresas activas, el almacenamiento de imágenes cuesta ~22 €/mes. Completamente asumible dado el MRR que generarían (~15.000-39.000 €/mes).

### Borrado de imágenes al eliminar un producto

```java
@EventListener
public void onProductDeleted(ProductDeletedEvent event) {
    List<ProductImage> images = productImageRepository.findByProductId(event.getProductId());
    for (ProductImage image : images) {
        r2Service.deleteObject(image.getR2Key());
    }
    productImageRepository.deleteAllByProductId(event.getProductId());
}
```

---

## 13. Stack tecnológico

### Backend

| Capa | Tecnología | Versión | Justificación |
|---|---|---|---|
| Lenguaje | Java | 25 (LTS) | Virtual Threads, Records, Pattern Matching, LTS |
| Framework | Spring Boot | 3.6.x | Ecosistema maduro, Spring Security, Data JPA |
| Seguridad | Spring Security + JWT | 6.x | Stateless, multi-tenant compatible |
| Base de datos | PostgreSQL | 17 | JSONB, Row Level Security, madurez |
| ORM | Spring Data JPA + Hibernate | 6.x | Queries tipadas, gestión de entidades |
| Migraciones | Flyway | 10.x | Control de versiones del esquema |
| Cache | Redis | 7.x | Sesiones y caché de reportes frecuentes |
| Cola de mensajes | RabbitMQ | 3.x | Alertas y reportes asíncronos |
| Email | JavaMail + Resend | — | Notificaciones transaccionales |
| Pagos | Stripe Java SDK | latest | Suscripciones recurrentes |
| Storage | AWS SDK v2 (S3-compatible) | 2.x | Cloudflare R2 para imágenes |
| Documentación API | SpringDoc OpenAPI (Swagger) | 2.x | Documentación automática |
| Rate limiting | Bucket4j | 8.x | Protección por IP y tenant |
| Export Excel | Apache POI | 5.x | Exportación de reportes |
| Export PDF | iText | 9.x | Exportación de reportes |
| Tests | JUnit 5 + Mockito + Testcontainers | — | Unitarios e integración |
| Contenedores | Docker + Docker Compose | — | Desarrollo local y despliegue |

### Frontend

| Capa | Tecnología | Versión | Justificación |
|---|---|---|---|
| Framework | React | 19 | Concurrent features, Server Components ready |
| Lenguaje | TypeScript | 5.x | Tipado estático, mantenibilidad |
| Build tool | Vite | 6.x | Velocidad de desarrollo |
| Estilos | **Tailwind CSS v4** | 4.x | CSS-first config, sin tailwind.config.js |
| Componentes UI | shadcn/ui (Tailwind v4) | latest | Accesibles, personalizables, composables |
| Routing | React Router | 7.x | SPA routing con lazy loading |
| Estado global | Zustand | 5.x | Simple, sin boilerplate |
| Server state | TanStack Query | 5.x | Cache de datos del servidor, invalidación |
| Tablas | TanStack Table | 8.x | Tablas avanzadas con filtros y paginación |
| Formularios | React Hook Form + Zod | — | Validación tipada |
| Gráficos | Recharts | 2.x | Dashboard y reportes visuales |
| Compresión imágenes | browser-image-compression | — | Comprime antes de subir a R2 |
| Internacionalización | i18next | — | Español, inglés, portugués |
| Notificaciones | Sonner | — | Toast notifications |
| Iconos | lucide-react | — | 1000+ iconos SVG |
| HTTP | Axios | — | Interceptores para JWT refresh |
| PWA | Vite PWA Plugin | — | Instalable en móvil |
| Tests | Vitest + Testing Library | — | Unitarios y componentes |

> **Tailwind v4 — diferencias clave respecto a v3:**
> - La configuración se hace **en CSS** (`@import "tailwindcss"`) en lugar de `tailwind.config.js`
> - Se usa el plugin `@tailwindcss/vite` en lugar de PostCSS
> - No hay archivo `content` que configurar — Tailwind v4 detecta automáticamente las clases usadas
> - shadcn/ui ofrece soporte oficial para Tailwind v4 desde su versión de 2025

### Infraestructura recomendada (coste inicial < 30 €/mes)

| Servicio | Proveedor | Coste estimado | Notas |
|---|---|---|---|
| Backend (Spring Boot) | Railway / Render | ~7 €/mes | Docker container |
| Frontend (React/Vite) | Vercel / Netlify | Gratis | CDN global incluido |
| PostgreSQL | Neon / Railway | ~7 €/mes | Serverless PostgreSQL |
| Redis | Upstash | Gratis (hasta 10k req/día) | Serverless Redis |
| Email transaccional | Resend | Gratis (3k emails/mes) | Para empezar |
| Almacenamiento imágenes | Cloudflare R2 | ~0 €/mes (10 GB gratis) | Egress gratis con CDN |
| CDN + DNS | Cloudflare | Gratis | Wildcard subdomain |
| Monitorización errores | Sentry | Gratis | 5k errores/mes |
| Uptime monitoring | Better Uptime / UptimeRobot | Gratis | Alertas si cae la app |

**Coste total inicial: ~14-20 €/mes.** Suficiente para las primeras 100-200 empresas activas.

---

## 14. Modelo de base de datos

### Esquema de entidades

```
business_types
companies (tenants)
    ├── branches (sucursales)
    ├── users (empleados con roles)
    ├── categories (jerarquía)
    ├── products
    │   ├── product_images (fotos en R2)
    │   ├── product_variants (tallas, colores...)
    │   └── stock_items (stock por variante y sucursal)
    │       └── stock_movements (historial)
    ├── suppliers (proveedores)
    ├── customers (plan Premium)
    ├── orders (pedidos registrados por empleados)
    │   ├── order_items (líneas del pedido)
    │   └── refunds (devoluciones)
    ├── expenses (gastos)
    │   └── expense_categories
    ├── alert_configs
    ├── alert_notifications
    └── audit_logs

-- Tablas de plataforma (fuera del tenant)
promo_codes
promo_code_usages
admin_actions
```

### Convenciones

- Todos los IDs son `UUID` con `gen_random_uuid()`
- Borrado lógico con `is_active = false` (nunca `DELETE` en tablas de negocio)
- Todas las tablas de negocio tienen `tenant_id UUID NOT NULL` con índice
- Índices mínimos:

```sql
CREATE INDEX idx_orders_tenant_date    ON orders(tenant_id, created_at DESC);
CREATE INDEX idx_products_tenant       ON products(tenant_id);
CREATE INDEX idx_stock_items_product   ON stock_items(product_id, branch_id);
CREATE INDEX idx_audit_tenant_date     ON audit_logs(tenant_id, created_at DESC);
CREATE INDEX idx_companies_stripe      ON companies(stripe_customer_id);
CREATE INDEX idx_promo_codes_code      ON promo_codes(code) WHERE is_active = TRUE;
CREATE INDEX idx_product_images_product ON product_images(product_id, sort_order);
```

---

## 15. Arquitectura de la aplicación

### Arquitectura hexagonal (Ports & Adapters)

```
src/
├── domain/
│   ├── model/                        # Entidades de dominio
│   ├── port/
│   │   ├── in/                       # Casos de uso (interfaces)
│   │   └── out/                      # Puertos de salida (repositorios)
│   └── service/                      # Implementación de casos de uso
├── infrastructure/
│   ├── persistence/                  # Adaptadores JPA
│   ├── web/
│   │   ├── company/                  # Endpoints de empresa (tenant)
│   │   ├── admin/                    # Endpoints del superadmin (/admin/api/v1/)
│   │   └── public/                   # Endpoints públicos (registro, promo codes)
│   ├── storage/                      # Cloudflare R2 (pre-signed URLs)
│   ├── messaging/                    # RabbitMQ: alertas y stock asíncronos
│   ├── external/                     # Stripe, email, WhatsApp
│   └── security/                     # JWT, TenantFilter, Spring Security
└── application/                      # DTOs, mappers, configuración Spring
```

### Eventos de dominio al registrar un pedido

```java
public class OrderService implements CreateOrderUseCase {
    public Order createOrder(CreateOrderCommand cmd) {
        Order order = buildOrder(cmd);
        orderRepository.save(order);

        // Procesamiento asíncrono vía RabbitMQ
        eventPublisher.publish(new OrderCreatedEvent(order));
        // → StockDeductionListener: descuenta stock de cada línea
        // → AlertCheckListener: verifica si algún producto bajó del mínimo
        // → FinanceListener: actualiza acumulados de ingresos del día

        return order;
    }
}
```

---

## 16. Seguridad

### Autenticación JWT

- **Access Token**: 15 minutos. Contiene `userId`, `tenantId`, `role`, `plan`, `impersonated`.
- **Refresh Token**: 7 días. Cookie `HttpOnly` (no accesible desde JavaScript).
- Rotación de refresh token en cada uso.

```json
{
  "sub": "user-uuid",
  "tenant": "company-uuid",
  "role": "EMPLOYEE",
  "plan": "STANDARD",
  "impersonated": false,
  "iat": 1718000000,
  "exp": 1718000900
}
```

### Medidas de seguridad

- **Rate limiting** por IP y por tenant (Bucket4j)
- **CORS** configurado estrictamente por dominio
- **Validación de inputs** en todos los endpoints (Bean Validation)
- **RLS de PostgreSQL** como segunda barrera de aislamiento entre tenants
- **HTTPS obligatorio** en producción
- **Contraseñas** con BCrypt (cost factor 12)
- **Auditoría de logins** sospechosos (nuevo dispositivo/IP)
- **Stripe Webhook Signature Verification** para prevenir webhooks falsos
- **Secrets** en variables de entorno, nunca en el código
- **Impersonación auditada**: cada sesión de impersonación queda en `audit_logs`

---

## 17. Funcionalidades extras opcionales

### CRM básico de clientes — Plan Premium

```sql
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(150),
    phone VARCHAR(20),
    notes TEXT,
    total_spent DECIMAL(10,2) DEFAULT 0,
    order_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### Gestión de proveedores — Plan Standard+

```sql
CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    contact_name VARCHAR(150),
    email VARCHAR(150),
    phone VARCHAR(20),
    tax_id VARCHAR(50),
    payment_terms VARCHAR(100),
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE
);
```

### Alertas por WhatsApp — Plan Premium

Mediante la API de Meta Cloud (WhatsApp Business API) o Twilio:

```java
@Service
public class WhatsAppNotificationService {
    public void sendAlert(String phoneNumber, String message) {
        // Meta Cloud API o Twilio WhatsApp API
    }
}
```

### API pública — Plan Premium

Para integraciones con tienda online, ERP u otras herramientas:

```
GET  /api/v1/public/products    → Lista de productos con stock
POST /api/v1/public/orders      → Registrar pedido desde canal externo
GET  /api/v1/public/stock       → Consultar stock en tiempo real
```

Autenticación con API Key generada por la empresa desde su configuración.

### Insights con IA — v2.0

Integración con Claude API para análisis automáticos en lenguaje natural:

> "Tu producto más rentable este mes fue X con un margen del 42%."
> "Los martes registras un 30% menos de pedidos. Considera una promoción semanal."

---

## 18. Expansión y escalabilidad sectorial

### Cómo añadir un nuevo sector (sin escribir código)

1. Insertar nuevo registro en `business_types` con `product_fields_config` y `default_alerts_config`
2. Insertar categorías predefinidas para ese sector
3. Añadir traducciones de UI para el nuevo vocabulario

Todo esto lo gestiona el SUPER_ADMIN desde su panel, sin nuevo despliegue.

### Sectores de expansión prioritaria

| Prioridad | Sector | Módulo adicional |
|---|---|---|
| Alta | Panadería/Obrador | Producción diaria, recetas |
| Alta | Ferretería | Referencias técnicas, catálogos grandes |
| Media | Hostelería | Comandas, mesas (v2.0) |
| Media | Salón de belleza | Citas, calendario |
| Baja | Taller mecánico | Órdenes de trabajo, vehículos |
| Baja | Gimnasio | Membresías, abonados |

### Módulos específicos con lazy loading (React)

```tsx
const BeautyModule = lazy(() => import("./modules/beauty"));

{company.businessType === "BEAUTY" && (
  <Suspense fallback={<Spinner />}>
    <BeautyModule />
  </Suspense>
)}
```

---

## 19. ¿Cuántas empresas necesito antes de lanzar?

### Análisis de costes y break-even

| Situación | Empresas | Ingresos/mes | Costes/mes | Resultado |
|---|:-:|:-:|:-:|:-:|
| Infraestructura sola | 0 | 0 € | ~20 € | -20 € |
| Cubre costes | 2 en Standard | 38 € | 20 € | +18 € |
| Break-even cómodo | 5 en Standard | 95 € | 22 € | +73 € |
| Reinversión posible | 15 mixtas | ~285 € | 25 € | +260 € |
| SaaS viable a tiempo parcial | 50 mixtas | ~950 € | 35 € | +915 € |

### Fase recomendada: beta privada

**Antes de lanzar al público**, busca **3-5 negocios reales** en tu entorno (amigos, familia, conocidos). Ofréceles:

- 3 meses completamente gratuitos (usa un código promo `BETA` de tipo `TRIAL_EXTENSION`)
- A cambio: feedback semanal real, reporte de bugs, uso activo del sistema

**Por qué al menos 3 empresas y no 1:**

- Una sola empresa podría tener necesidades muy específicas que no son representativas
- Con 3 empresas de **sectores diferentes** validas que el modelo de sectores funciona
- Detectas bugs que solo aparecen en uso real (no en pruebas) antes del lanzamiento público
- Tienes primeros testimonios/casos de éxito para la landing page

**Cuándo lanzar públicamente:**

1. El sistema lleva 2-4 semanas sin bugs críticos con las empresas beta
2. El onboarding es comprensible sin ayuda (las empresas se configuraron solas)
3. Stripe está en modo live (no test)
4. Tienes los textos legales publicados (aviso legal, privacidad, condiciones)
5. Tienes al menos 1 sector completamente funcional y otro parcial

**Hito real de tracción:** 10 empresas pagando. A partir de ahí tienes validación de producto y puedes invertir en marketing con confianza.

---

## 20. Soporte remoto sin visitas físicas

Este es un principio de diseño, no solo una característica. Aquí está el inventario completo de herramientas que tienes para resolver cualquier problema sin desplazarte:

### Herramientas disponibles desde el panel de superadmin

| Problema del cliente | Tu solución remota |
|---|---|
| "No recuerdo mi contraseña" | El cliente usa "Olvidé mi contraseña" de forma autónoma |
| "Bloqueé mi cuenta" | Reactivar usuario desde el panel de superadmin |
| "No veo mis pedidos" | Impersonar su cuenta → ver exactamente lo que él ve |
| "Un empleado borró productos" | Consultar `audit_logs` → ver qué pasó y cuándo |
| "El stock no cuadra" | Ver `stock_movements` de la empresa en modo lectura |
| "No puedo pagar" | Ver historial de pagos en Stripe desde el panel + enviar email manual |
| "Quiero cancelar" | El cliente cancela solo desde Stripe Customer Portal |
| "Necesito más tiempo para decidir" | Extender el trial desde el panel (30 segundos) |
| "Tenemos una segunda tienda" | Cambiar el plan desde el panel si necesitan Standard |
| "La app no carga" | Sentry te avisa antes de que el cliente te contacte |
| "Necesito exportar todos mis datos" | Exportación de datos desde la app (Excel/PDF) por el propio cliente |

### Monitorización proactiva (tú te enteras antes que el cliente)

- **Sentry**: te avisa en tiempo real de cualquier error no controlado
- **Better Uptime / UptimeRobot**: alerta en < 1 minuto si la app cae
- **Stripe Dashboard**: alertas de pagos fallidos y renovaciones próximas
- **Spring Boot Actuator** + `/admin/api/v1/dashboard`: métricas de salud en tiempo real

### Recuperación de datos

Nunca se borra nada definitivamente:

- Los productos se desactivan (`is_active = false`), no se borran
- Los empleados se desactivan, no se eliminan
- Los pedidos cancelados siguen en la base de datos
- La auditoría guarda old_value + new_value de cada cambio

Si un cliente dice "alguien borró mis productos", puedes ver en `audit_logs` exactamente qué pasó, a qué hora, desde qué IP y desde qué dispositivo.

### SLA realista para un SaaS individual

| Nivel | Tiempo de respuesta | Canal |
|---|---|---|
| Basic | 48h hábiles | Email |
| Standard | 24h hábiles | Email |
| Premium | 4h (horario laboral) | Email / Chat |
| Crítico (sistema caído) | Automático | Sentry + monitor de uptime |

---

## 21. Estrategia de venta y marketing

### Propuesta de valor

> "BizCore es la herramienta que necesita tu negocio para llevar un control real de pedidos, stock y finanzas, sin instalar nada, desde cualquier móvil u ordenador. Tus empleados registran los pedidos en segundos, y tú ves los resultados en tiempo real desde donde estés."

### Ventaja clave: cero instalación

- No hay nada que instalar ni hardware que comprar
- Funciona en el móvil, tablet u ordenador que ya tienes
- Empiezas en 10 minutos desde cualquier lugar

### Sectores objetivo iniciales

1. **Tiendas de ropa independientes**: 50.000+ en España. Problema clave: gestión de tallas, colores y stock.
2. **Panaderías y pastelerías artesanales**: muy fragmentadas, bajo nivel de digitalización.
3. **Ferreterías**: catálogos grandes difíciles de gestionar con Excel.

### Canales de adquisición

| Canal | Estrategia | Coste |
|---|---|---|
| SEO | Blog: "Cómo controlar el stock de tu tienda", "Gestión de pedidos sin Excel" | Bajo |
| Google Ads | Keywords: "gestión pedidos tienda web", "control stock pyme" | Medio |
| Instagram/TikTok | Demostración del dashboard y registro de pedidos | Bajo |
| Referidos | 1 mes gratis por empresa referida que se suscriba (código promo automático) | Bajo |
| Asociaciones gremiales | Acuerdos con asociaciones de comerciantes (código promo exclusivo) | Bajo |
| Cold email | Propietarios de tiendas en Google Maps | Muy bajo |

### Psicología de pricing

- Plan **Standard** como "el más popular" (el que más interesa vender)
- **Trial de 14 días sin tarjeta**: máxima reducción de fricción inicial
- **Pago anual con descuento del 20%**: mejora el flujo de caja y reduce el churn
- Enmarcar en ROI: "Por menos de un café al día, controlas todo tu negocio"

### Métricas clave

| Métrica | Objetivo Año 1 |
|---|---|
| MRR | 5.000 € (~260 clientes en Standard) |
| Churn mensual | < 3% |
| CAC | < 30 € |
| LTV | > 300 € (15+ meses) |
| Conversión trial → paid | > 25% |

---

## 22. Roadmap de desarrollo

### Fase 0 — Fundamentos (Semanas 1-2)

- [ ] Setup Spring Boot 3.6 con arquitectura hexagonal
- [ ] Setup React 19 + TypeScript + Tailwind v4 + shadcn/ui
- [ ] Docker Compose: PostgreSQL + Redis + RabbitMQ
- [ ] Multi-tenant: RLS en PostgreSQL + TenantFilter en Spring
- [ ] Autenticación JWT con refresh tokens en cookie HttpOnly
- [ ] Sistema de roles y permisos (Spring Security + @PreAuthorize)
- [ ] Integración Stripe: suscripciones + webhooks + Customer Portal
- [ ] Registro de empresas con wizard de onboarding + selector de sector
- [ ] Sistema de códigos promocionales (tabla + validación + aplicación)
- [ ] Emails transaccionales: bienvenida, trial expirando, pago confirmado

### Fase 1 — MVP (Semanas 3-6)

- [ ] CRUD de categorías y productos (con campos dinámicos por sector)
- [ ] Upload de imágenes de producto via R2 pre-signed URLs + compresión cliente
- [ ] Gestión de stock (consulta, ajustes manuales, historial)
- [ ] Registro de pedidos por empleados (formulario web con buscador de productos)
- [ ] Descuento automático de stock al registrar pedido
- [ ] Dashboard básico: pedidos del día, stock bajo, ingresos del mes
- [ ] Alertas de stock bajo por email
- [ ] Gestión de empleados (alta, baja, roles, invitación por email)
- [ ] BusinessTypes: `RETAIL_FASHION` + `GENERIC`
- [ ] **Panel superadmin v1**: lista de empresas + detalle + suspender/reactivar + gestión de promo codes

### Fase 2 — Producto completo (Semanas 7-12)

- [ ] Gestión de gastos e ingresos con categorías
- [ ] Reportes: pedidos por período, productos más pedidos, P&L mensual
- [ ] Exportación a Excel y PDF
- [ ] Gestión de proveedores
- [ ] BusinessTypes: `BAKERY_FOOD` + `HARDWARE`
- [ ] Múltiples sucursales (plan Standard+)
- [ ] Auditoría completa con retención por plan
- [ ] **Panel superadmin v2**: métricas globales (MRR, churn, conversión) + acciones manuales + impersonación
- [ ] Recuperación de contraseña automática

### Fase 3 — Crecimiento (Semanas 13-20)

- [ ] CRM básico de clientes (plan Premium)
- [ ] Alertas por WhatsApp (Meta Cloud API, plan Premium)
- [ ] Reportes avanzados y tendencias históricas
- [ ] API pública con autenticación por API Key (plan Premium)
- [ ] PWA: instalable en móvil
- [ ] BusinessTypes: `PHARMACY` + `BEAUTY`
- [ ] **Panel superadmin v3**: configuración de BusinessTypes desde la UI + plantillas de email

### Fase 4 — Escala (Mes 6+)

- [ ] Insights automáticos con IA (Claude API)
- [ ] App móvil nativa (React Native o Capacitor)
- [ ] Módulo de hostelería (`RESTAURANT_BAR`): comandas y mesas
- [ ] Integración con e-commerce (Shopify, WooCommerce)
- [ ] Facturación electrónica (TicketBAI para España)

---

## Apéndice A — Dependencias Spring Initializr

### Paso 1: Configuración en start.spring.io

```
Project:    Maven
Language:   Java
Spring Boot: 3.6.x (latest)
Group:      com.bizcore
Artifact:   bizcore-backend
Packaging:  Jar
Java:       25
```

### Paso 2: Dependencias a seleccionar en Spring Initializr

```
Web:
  ✅ Spring Web

SQL:
  ✅ Spring Data JPA
  ✅ PostgreSQL Driver
  ✅ Flyway Migration

Security:
  ✅ Spring Security

Messaging:
  ✅ Spring for RabbitMQ
  ✅ Spring Data Redis (Lettuce)

I/O:
  ✅ Validation
  ✅ Java Mail Sender

Ops:
  ✅ Spring Boot Actuator

Developer Tools:
  ✅ Lombok
  ✅ Spring Boot DevTools (solo dev)
```

### Paso 3: Dependencias a añadir manualmente en pom.xml

```xml
<!-- ===== JWT ===== -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<!-- ===== Stripe ===== -->
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>27.1.0</version>
</dependency>

<!-- ===== OpenAPI / Swagger UI ===== -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>

<!-- ===== Rate Limiting ===== -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-redis</artifactId>
    <version>8.10.1</version>
</dependency>

<!-- ===== Export Excel ===== -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.3.0</version>
</dependency>

<!-- ===== Export PDF ===== -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>kernel</artifactId>
    <version>9.0.0</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>layout</artifactId>
    <version>9.0.0</version>
</dependency>

<!-- ===== Cloudflare R2 (S3-compatible) ===== -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.29.0</version>
</dependency>
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3-presigner</artifactId>
    <version>2.29.0</version>
</dependency>

<!-- ===== Email con Resend (alternativa a JavaMail) ===== -->
<dependency>
    <groupId>com.resend</groupId>
    <artifactId>resend-java</artifactId>
    <version>3.1.0</version>
</dependency>

<!-- ===== WhatsApp (Twilio) - solo Plan Premium ===== -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>10.6.4</version>
</dependency>

<!-- ===== Testcontainers ===== -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>rabbitmq</artifactId>
    <scope>test</scope>
</dependency>
```

### Paso 4: Configuración del BOM de AWS SDK (en dependencyManagement)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>bom</artifactId>
            <version>2.29.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

## Apéndice B — Setup del frontend (comandos npm)

### Paso 1: Crear el proyecto con Vite + React 19 + TypeScript

```bash
npm create vite@latest bizcore-frontend -- --template react-ts
cd bizcore-frontend
```

### Paso 2: Instalar Tailwind CSS v4 (CSS-first, sin tailwind.config.js)

```bash
npm install tailwindcss @tailwindcss/vite
```

Añadir el plugin en `vite.config.ts`:

```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
})
```

En `src/index.css` (reemplazar todo el contenido):

```css
@import "tailwindcss";
```

### Paso 3: Routing y estado

```bash
# Routing
npm install react-router-dom

# Estado global (simple, sin boilerplate)
npm install zustand

# Server state, caché y sincronización
npm install @tanstack/react-query @tanstack/react-query-devtools
```

### Paso 4: Formularios y validación

```bash
npm install react-hook-form zod @hookform/resolvers
```

### Paso 5: Inicializar shadcn/ui (compatible con Tailwind v4)

```bash
npx shadcn@latest init
```

> Seleccionar: TypeScript, React, estilo "New York" o "Default", Tailwind v4.
> Esto crea `components.json` y la carpeta `src/components/ui/`.

Instalar componentes shadcn/ui necesarios:

```bash
npx shadcn@latest add button input label card badge dialog sheet
npx shadcn@latest add table dropdown-menu select checkbox
npx shadcn@latest add toast sonner calendar date-picker
npx shadcn@latest add sidebar navigation-menu tabs
```

### Paso 6: Gráficos y tablas

```bash
# Gráficos para el dashboard
npm install recharts

# Tablas avanzadas con filtros, paginación, ordenación
npm install @tanstack/react-table
```

### Paso 7: Utilidades

```bash
# Cliente HTTP con interceptores para JWT refresh
npm install axios

# Fechas
npm install date-fns

# Iconos SVG (1000+)
npm install lucide-react

# Toast notifications (alternativa a shadcn toast)
npm install sonner

# Utilidades de clases CSS
npm install clsx tailwind-merge class-variance-authority
```

### Paso 8: Funcionalidades específicas de BizCore

```bash
# Compresión de imágenes en el cliente antes de subir a R2
npm install browser-image-compression

# Internacionalización (español, inglés, portugués)
npm install i18next react-i18next i18next-browser-languagedetector i18next-http-backend
```

### Paso 9: PWA (instalable en móvil)

```bash
npm install -D vite-plugin-pwa workbox-window
```

### Paso 10: Testing

```bash
npm install -D vitest @testing-library/react @testing-library/jest-dom @testing-library/user-event jsdom @vitest/coverage-v8
```

### Paso 11: Dev tools adicionales

```bash
npm install -D @types/node
```

### Resumen del package.json (dependencies finales)

```json
{
  "dependencies": {
    "@hookform/resolvers": "^3.x",
    "@tanstack/react-query": "^5.x",
    "@tanstack/react-query-devtools": "^5.x",
    "@tanstack/react-table": "^8.x",
    "axios": "^1.x",
    "browser-image-compression": "^2.x",
    "class-variance-authority": "^0.7.x",
    "clsx": "^2.x",
    "date-fns": "^4.x",
    "i18next": "^23.x",
    "i18next-browser-languagedetector": "^7.x",
    "i18next-http-backend": "^2.x",
    "lucide-react": "latest",
    "react": "^19.x",
    "react-dom": "^19.x",
    "react-hook-form": "^7.x",
    "react-i18next": "^14.x",
    "react-router-dom": "^7.x",
    "recharts": "^2.x",
    "sonner": "^1.x",
    "tailwind-merge": "^2.x",
    "workbox-window": "^7.x",
    "zod": "^3.x",
    "zustand": "^5.x"
  },
  "devDependencies": {
    "@tailwindcss/vite": "^4.x",
    "@testing-library/jest-dom": "^6.x",
    "@testing-library/react": "^16.x",
    "@testing-library/user-event": "^14.x",
    "@types/node": "^22.x",
    "@types/react": "^19.x",
    "@types/react-dom": "^19.x",
    "@vitejs/plugin-react": "^4.x",
    "@vitest/coverage-v8": "^2.x",
    "jsdom": "^25.x",
    "tailwindcss": "^4.x",
    "typescript": "^5.x",
    "vite": "^6.x",
    "vite-plugin-pwa": "^0.21.x",
    "vitest": "^2.x"
  }
}
```

---

## Apéndice C — Variables de entorno

```env
# ===== Base de datos =====
DATABASE_URL=jdbc:postgresql://localhost:5432/bizcore
DATABASE_USERNAME=bizcore_user
DATABASE_PASSWORD=...

# ===== JWT =====
JWT_SECRET=...
JWT_ACCESS_TOKEN_EXPIRATION=900000       # 15 minutos en ms
JWT_REFRESH_TOKEN_EXPIRATION=604800000   # 7 días en ms

# ===== Stripe =====
STRIPE_SECRET_KEY=sk_live_...
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_BASIC_PRICE_ID=price_...
STRIPE_STANDARD_PRICE_ID=price_...
STRIPE_PREMIUM_PRICE_ID=price_...
STRIPE_BASIC_ANNUAL_PRICE_ID=price_...
STRIPE_STANDARD_ANNUAL_PRICE_ID=price_...
STRIPE_PREMIUM_ANNUAL_PRICE_ID=price_...

# ===== Email transaccional (Resend) =====
RESEND_API_KEY=re_...
MAIL_FROM=noreply@bizcore.app

# ===== Redis =====
REDIS_URL=redis://localhost:6379

# ===== RabbitMQ =====
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=...
RABBITMQ_PASSWORD=...

# ===== WhatsApp (Twilio) — plan Premium =====
TWILIO_ACCOUNT_SID=...
TWILIO_AUTH_TOKEN=...
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886

# ===== Cloudflare R2 (imágenes) =====
CLOUDFLARE_R2_BUCKET=bizcore-uploads
CLOUDFLARE_R2_ACCESS_KEY=...
CLOUDFLARE_R2_SECRET_KEY=...
CLOUDFLARE_R2_ENDPOINT=https://ACCOUNT_ID.r2.cloudflarestorage.com
CLOUDFLARE_R2_PUBLIC_URL=https://cdn.bizcore.app

# ===== URLs de la aplicación =====
APP_LANDING_URL=https://bizcore.app
APP_URL=https://app.bizcore.app
APP_ADMIN_URL=https://admin.bizcore.app

# ===== Sentry (monitorización de errores) =====
SENTRY_DSN=https://...@sentry.io/...
```

---

*BizCore SaaS — Documento de diseño y desarrollo v2.0*
*Stack: Java 25 · Spring Boot 3.6 · React 19 · Tailwind v4 · PostgreSQL · Multi-tenant*
