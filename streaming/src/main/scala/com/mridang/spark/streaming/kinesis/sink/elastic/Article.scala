package com.mridang.spark.streaming.kinesis.sink.elastic

case class Article
  (
    url: String,
    product_id: String,
    name: String,
    image_url: String,
    price_currency_code: String,
    availability: String,
    rating_value: String,
    review_count: String,
    categories: List[String],
    description: String,
    price: Double,
    list_price: Double,
    brand: String,
    tag1: List[String],
    tag2: List[String],
    tag3: List[String],
    date_published: String,
    alternate_image_urls: List[String],
    inventory_level: Double,
    supplier_cost: Double
  )