with
topsel0 as (
    /*@{cutempty}*//*[p_calcFrom]*/
    select a.pu_num,
           sum(a.tckts) as tckts,
           sum(a.summ) as summ,
           sum(a.sess) as sess,
           count(distinct a.org_id) as orgs
      from CUB5$SALES1 a
     where a.part_month between to_char(:p_calcFrom, 'YYYYMM') and to_char(:p_calcTo, 'YYYYMM')
       and a.show_date >= trunc(:p_calcFrom) and a.show_date < trunc(:p_calcTo)+1
       and (
            (:p_userRole is null)
            or (:p_userRole in ('5', '6', '8'))
            or (
                (:p_userRole in ('7'))
                and (
                    exists (
                        select 1
                           from ORGM$FILMPERMWS e
                          where e.id_org = to_number(:p_userOrgId)
                            and e.pu_number = a.pu_num
                    )
                )
            )
        )
     group by a.pu_num
    /*{cutempty}@*/
    /*@{cutnotempty}*//*[p_calcFrom]*/
    select a.pu_num,
           a.tckts,
           a.summ,
           a.sess,
           a.orgs
      from CUB5$TOP1 a
     where (
            (:p_userRole is null)
            or (:p_userRole in ('5', '6', '8'))
            or (
                (:p_userRole in ('7'))
                and (
                    exists (
                        select 1
                           from ORGM$FILMPERMWS e
                          where e.id_org = to_number(:p_userOrgId)
                            and e.pu_number = a.pu_num
                    )
                )
            )
        )
     group by a.pu_num
    /*{cutnotempty}@*/
)
select
        k.name_rus as fname, --Название фильма
        k.mdistributor as rightholders, --Название Правообладателя
        a.pu_num as movieId,  --Номер ПУ
        a.orgs as cinemaCount,      --Кол-во кинотеатров
        a.summ as boxOffice,   --Сборы в рублях
        a.tckts as viewers,     --Зрители
        a.sess as seancesСount,     --Кол-во сеансов
        k.genre as genre, --Жанр
        k.madein as country, --Страна
        k.flmcategory as category, --Категория
        raoc_api01.encodeDate(k.puissued) as certRegDate,  --dd.mm.YYYY, Дата выдачи ПУ
        raoc_api01.encodeDate(k.startdate) as releaseDate,  --dd.mm.YYYY, Дата релиза
        k.dur_hours * 60 + k.dur_mins as fduration,      --Продолжительность фильма в минутах
        k.annotxt as description, --Описание
        k.studia as production, --Производство
        k.rntcategory as rentCategory  --Категория проката
from TOPSEL0 a
    inner join CUB5$KINOSM0 k on k.pu_num = a.pu_num
where /*@{cutempty}*/(k.startdate >= trunc(:p_releaseFrom) and a.show_date < trunc(:p_releaseTo)+1) and/*{cutempty}@*/
/*@{cutempty}*/(k.pu_num = :p_movieId) and/*{cutempty}@*/
/*@{cutempty}*/(lower(k.name_rus) like '%'||lower(:p_cinemaName)||'%') and/*{cutempty}@*/
/*@{cutempty}*/(lower(k.prod_year)  like '%'||lower(:p_releaseYear)||'%') and/*{cutempty}@*/
/*@{cutempty}*/(lower(k.genre)  like '%'||lower(:p_genre)||'%') and/*{cutempty}@*/
/*@{cutempty}*/(lower(k.madein)  like '%'||lower(:p_country)||'%') and/*{cutempty}@*/
/*@{cutempty}*/(lower(k.studia)  like '%'||lower(:p_production)||'%') and/*{cutempty}@*/
/*@{cutempty}*/(lower(k.age_restr)  like '%'||lower(:p_ageRestriction)||'%') and/*{cutempty}@*/
/*@{cutempty}*/(lower(k.mdistributor)  like '%'||lower(:p_rightholders)||'%') and/*{cutempty}@*/
1=1

