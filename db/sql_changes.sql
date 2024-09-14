-- Jose Prado 15/11/23 14:17 -- 
--------------------------------
ALTER TABLE usuario DROP CONSTRAINT cedula_uk;


-- Angel Ojeda 21/12/23 13:17 -- 
ALTER TABLE public.auditoria ALTER COLUMN detalle TYPE text USING detalle::text;


-- Angel Ojeda 22/12/23 09:10 --
-- Se crea la secuencia para planes
CREATE SEQUENCE public.plan_id_plan_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

-- Se agrega la secuencia a la tabla de plan
ALTER TABLE public.plan ALTER COLUMN id_plan SET DEFAULT nextval('plan_id_plan_seq'::regclass);

-- Asigna el ultimo valor del id al secuenciador
SELECT setval('public.permiso_permiso_id_seq', (SELECT MAX(id) FROM permiso));

-- Asigna el ultimo valor del id al secuenciador
SELECT setval('public.auditoria_id_auditoria_seq', (SELECT MAX(id_auditoria) FROM auditoria));

-- Asigna el ultimo valor del id al secuenciador
SELECT setval('public.sistema_sistema_id_seq', (SELECT MAX(id_sistema) FROM sistema));

-- Asigna el ultimo valor del id al secuenciador
-- Angel Ojeda 26/12/23 09:10 --
SELECT setval('public.rol_rol_id_seq', (SELECT MAX(id) FROM rol));

-- Asigna el ultimo valor del id al secuenciador
-- Angel Ojeda 29/12/23 10:10 --
ALTER SEQUENCE usuario_usuario_id_seq RESTART WITH 27.754;

-- Angel Ojeda 18/12/23 10:10 --
TRUNCATE TABLE auditoria RESTART IDENTITY RESTRICT;






