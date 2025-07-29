# CoreTrack-business-resource-management-system

1. Product Inventory Controller (/api/product-inventory)
TypeEndpointReceiveReturnPurposeGET/init@Body: AddProductInventoryRequestAddProductInventoryResponseInitial the stock for an available productPUT/{variantId}/set@PathVariable: Long variantId 
@RequestBody:  StockSetRequestInventoryTransactionResponseSet the current stock of a particular variantIdPUT/{variantId}/add@PathVariable:  Long variantId
@RequestBody:  StockModifyRequestInventoryTransactionResponseAdd a number of stock to the current number of stockPUT/{variantId}/subtract@PathVariable:  Long variantId
@RequestBody:  StockModifyRequestInventoryTransactionResponseSubstract a numver of stock from current stockGET/filter@RequestParam: search
@RequestParam: List<String> groupProducts
@RequestParam: List<String> inventoryStatus
@PageableDefault: pageable Page<SearchProductInventoryResponse>
To filter the product inventory dataset and receive the product inventory that meet the requirementGET/autocomplete@RequestParam: searchList<AllSearchProductInventoryResponse>Use the display the product inventory when the user search on the search bar
