SELECT 0 as sbkitem_id, ''::text as sbook_name,
        to_char(max(to_number(substring(a.sbknum from '\d+/'), '99999999999'))+1, 'FM999999999999')||'/'||to_char(current_date, 'YYYY') as sbknum,
        null::numeric(18) as sbkdoctype_id,
        ''::text as docnum,
        current_date as docdate,
        ''::text as adesc
  FROM efond2_cat.sbkitem a
WHERE substring(a.sbknum from '(?<=/)[1,2][9,0,1,2]\d{2}') = to_char(current_date, 'YYYY')
