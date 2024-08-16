CREATE OR REPLACE FUNCTION referencedata.fill_rate_3_fn(program uuid, facility uuid, from_date text, to_date text)
 RETURNS TABLE(program_name text, program_id uuid, supplying_facility text, receiving_facility text, processing_period text, processing_period_id uuid, ordercode text, order_id uuid, items_ordered bigint, items_fullfilled bigint, fill_rate text)
 LANGUAGE sql
AS $function$



 WITH a AS (
         SELECT p.name AS program_name,
            p.id AS program_id,
            f.name AS supplying_facility,
            rf.name AS receiving_facility,
            pp.name AS processing_period,
            pp.id AS processing_period_id,
            replace(o.ordercode, 'ORDER-'::text, ''::text) AS ordercode,
            o.id AS order_id,
            count(DISTINCT oli.orderableid) AS items_ordered,
            count(DISTINCT sli.orderableid) AS items_fullfilled
           FROM fulfillment.orders o
             JOIN fulfillment.order_line_items oli ON oli.orderid = o.id
             JOIN fulfillment.shipments s ON s.orderid = o.id
             JOIN fulfillment.shipment_line_items sli ON sli.shipmentid = s.id
             JOIN referencedata.programs p ON p.id = o.programid
             JOIN referencedata.facilities f ON f.id = o.supplyingfacilityid
             JOIN referencedata.facilities rf ON rf.id = o.receivingfacilityid
             JOIN referencedata.processing_periods pp ON pp.id = o.processingperiodid
          WHERE (o.status::text = ANY (ARRAY['SHIPPED'::character varying, 'RECEIVED'::character varying]::text[])) AND oli.orderedquantity > 0 AND sli.quantityshipped > 0 AND p.id=$1 AND f.id=$2
	 	and o.createddate between TO_DATE($3,'DD-MM-YYYY') and   TO_DATE($4,'DD-MM-YYYY')
          GROUP BY p.name, p.id, f.name, rf.name, pp.name, pp.id, o.ordercode, o.id
          ORDER BY pp.startdate DESC
        )
 SELECT a.program_name,
    a.program_id,
    a.supplying_facility,
    a.receiving_facility,
    a.processing_period,
    a.processing_period_id,
    a.ordercode,
    a.order_id,
    a.items_ordered,
    a.items_fullfilled,
    concat(a.items_fullfilled * 100 / a.items_ordered, '%') AS fill_rate
   FROM a

$function$
;