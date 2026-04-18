# Changelog

## [1.2.0](https://github.com/FelipeBoos/PriceLab/compare/v1.1.0...v1.2.0) (2026-04-18)


### Features

* add currency exchange and import cost support ([b1c7fb7](https://github.com/FelipeBoos/PriceLab/commit/b1c7fb7679b6aecc723f0e486f686e7f6b8068f0))
* **backend:** complete Frankfurter external API implementation for exchange rate service ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([527e61f](https://github.com/FelipeBoos/PriceLab/commit/527e61f62f81457334a4d6121d055bddaf5ab9ea))
* **entity:** add importation fields to Produto entity and update repository tests ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([3afb4f6](https://github.com/FelipeBoos/PriceLab/commit/3afb4f65866c3c226cba1ee5a4eaf0761d8a87cf))
* **importacao:** create import cost calculation service ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([c764f56](https://github.com/FelipeBoos/PriceLab/commit/c764f56cc45ffcb9a59c2d08b28d45dc476ceb5f))
* **produto/db:** add import cost fields to produtos table ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([777eec2](https://github.com/FelipeBoos/PriceLab/commit/777eec207121d6adfe77c5a03fa22fbc8fd0d6e9))
* **produto:** add importation inputs and cost summary to product form ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([5930cdf](https://github.com/FelipeBoos/PriceLab/commit/5930cdfcca4e64bfb563e2840e3efaa58dd66bc1))
* **produto:** support importation fields in DTOs, service and controller tests ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([c571d46](https://github.com/FelipeBoos/PriceLab/commit/c571d469a7a7ff0bf86651fcd93a8f150a1ecb1a))


### Bug Fixes

* **ui:** adjust category form layout and improve product form responsiveness ([340b42a](https://github.com/FelipeBoos/PriceLab/commit/340b42a37bc4896e53a15acec1008fe86a00f973))

## [1.1.0](https://github.com/FelipeBoos/PriceLab/compare/v1.0.1...v1.1.0) (2026-04-10)


### Features

* **frontend:** add category tag for product in price strategy simulation page ([7989f56](https://github.com/FelipeBoos/PriceLab/commit/7989f5697bae32f695d905cb975dc95d59d72895))

## [1.0.1](https://github.com/FelipeBoos/PriceLab/compare/v1.0.0...v1.0.1) (2026-04-09)


### Bug Fixes

* **frontend:** fix profit margin analysis indicator in price simulation page to display the correct value ([52831ec](https://github.com/FelipeBoos/PriceLab/commit/52831eca7506fb36f83ead7c0e0540d83d0bcd03))

## 1.0.0 (2026-04-09)


### Features

* **backend/estrategia-preco:** add missing fields to price strategy response ([#1](https://github.com/FelipeBoos/PriceLab/issues/1)) ([f784d84](https://github.com/FelipeBoos/PriceLab/commit/f784d8481b92a0ec9b19d9afe77e2086857722dc))
* **backend/excecao:** add custom exception classes ([#2](https://github.com/FelipeBoos/PriceLab/issues/2)) ([a16f9ff](https://github.com/FelipeBoos/PriceLab/commit/a16f9ff63a7f92559c282d9af601268ab07390dd))
* **backend/exceptions:** complete custom exception handling with global handler and service integration (closes [#2](https://github.com/FelipeBoos/PriceLab/issues/2)) ([b329fbe](https://github.com/FelipeBoos/PriceLab/commit/b329fbef5aa5b4aa32d26f9e91107f27d38ecfe2))
* **backend/handler:** implement global exception handler ([#2](https://github.com/FelipeBoos/PriceLab/issues/2)) ([0abdb68](https://github.com/FelipeBoos/PriceLab/commit/0abdb6851ed92fc6c1f26445f1ed9b9e537da297))
* **backend:** add currency support to Produto with migration and test updates ([b6145f8](https://github.com/FelipeBoos/PriceLab/commit/b6145f80369edd34f935743f7d42a1e2842aca1b))
* **backend:** add exchange rate service contract and currency conversion base logic ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([ff0c5f8](https://github.com/FelipeBoos/PriceLab/commit/ff0c5f88e080a3c4e444b4c33ceaa395c735bc51))
* **categoria:** add endpoint to list all categories ([79291d5](https://github.com/FelipeBoos/PriceLab/commit/79291d569254634ab8382eff619c2fbb0fbe0ed8))
* **categoria:** implement category creation from frontend with backend integration ([a7f6abf](https://github.com/FelipeBoos/PriceLab/commit/a7f6abf18d772236209f435641edc3f1b420af15))
* **categoria:** implement category deletion from frontend ([e00de45](https://github.com/FelipeBoos/PriceLab/commit/e00de453625085bb4daa7745f81c48f4ab9dc07d))
* **categoria:** implement category listing in Angular frontend ([9453cba](https://github.com/FelipeBoos/PriceLab/commit/9453cba3187987413fecf3b4eab0bf40f371e290))
* **categoria:** implement category update in frontend ([4c12178](https://github.com/FelipeBoos/PriceLab/commit/4c121782b898243fd5bccb210132845f6e4f24e3))
* **categoria:** prevent deletion of categories with associated products ([5ef051c](https://github.com/FelipeBoos/PriceLab/commit/5ef051c9c30af0fb78fb00839be3049b788b3547))
* **dto:** add CategoriaRequestDTO ([1018150](https://github.com/FelipeBoos/PriceLab/commit/101815063ceb0f5232c4ac2bb63a8bc602d91374))
* **dto:** add CategoriaResponseDTO ([0369045](https://github.com/FelipeBoos/PriceLab/commit/0369045a605f632f14d19a3b4d778cefaa5fe7e5))
* **dto:** add ErroResponseDTO for global error handling ([#2](https://github.com/FelipeBoos/PriceLab/issues/2)) ([99e1d13](https://github.com/FelipeBoos/PriceLab/commit/99e1d135e31d3b8713095146a892d3647abd6e48))
* **dto:** add EstrategiaPrecoUpdateDTO ([63f8814](https://github.com/FelipeBoos/PriceLab/commit/63f88140dbb0bb7f1ef9ad2ede071f99b2abadb2))
* **dto:** add RequestDTO, ResponseDTO and UpdateDTO for entity Produto ([90266d8](https://github.com/FelipeBoos/PriceLab/commit/90266d8eabdd84d7062278abfd5a6147c205725a))
* **Entity:** add Categoria entity, repository, service and controller ([3b40558](https://github.com/FelipeBoos/PriceLab/commit/3b40558331a7421992324fbd4b8022baf299382b))
* **Entity:** add Moeda entity ([#5](https://github.com/FelipeBoos/PriceLab/issues/5)) ([e771483](https://github.com/FelipeBoos/PriceLab/commit/e771483587d92d67ef17dcee3ae0361655799ba1))
* **frontend/estrategia-preco:** display additional fields in simulation results (closes [#1](https://github.com/FelipeBoos/PriceLab/issues/1)) ([ae52a44](https://github.com/FelipeBoos/PriceLab/commit/ae52a44af49788460c497e50f2461c40f48f357d))
* **frontend/preco-page:** add product data card to simulation results ([f82d1e0](https://github.com/FelipeBoos/PriceLab/commit/f82d1e0a2773a582ba5c2bbb8cc594b59c66a2a7))
* **frontend/preco-page:** display simulation result in modal ([e0d1b38](https://github.com/FelipeBoos/PriceLab/commit/e0d1b38ad636689f6d0a390b235beff2812960d8))
* **frontend/preco-page:** implement price strategy simulation result cards ([f0f49be](https://github.com/FelipeBoos/PriceLab/commit/f0f49be390b67673ef5eaebe4e76ac8c5611fa2e))
* **frontend/preco-page:** integrate price strategy simulation form ([b210059](https://github.com/FelipeBoos/PriceLab/commit/b210059e89c4f2f6a03061011b051dc7a622c2b0))
* **frontend/preco-service:** add method to create price strategy ([8dd0e0b](https://github.com/FelipeBoos/PriceLab/commit/8dd0e0b15e0733738190985d320d5a02bbecad90))
* **frontend/price-service:** add method do list all price strategies ([ec10c7c](https://github.com/FelipeBoos/PriceLab/commit/ec10c7cea6ab93c852b64534b4388b2d87081cc4))
* **frontend/price-service:** add method to delete price strategies ([78d2c5e](https://github.com/FelipeBoos/PriceLab/commit/78d2c5e88492781828747dec911078999ba36a79))
* **frontend/price-strategy:** add initial page for price strategy simulation ([a4d5f32](https://github.com/FelipeBoos/PriceLab/commit/a4d5f32b0c06cfb3f1895087d30928fcecdf72ea))
* **frontend:** add base test layout for new price strategy simulation page ([#4](https://github.com/FelipeBoos/PriceLab/issues/4)) ([1785649](https://github.com/FelipeBoos/PriceLab/commit/17856499378e50c29573d8a11166507da7e5b761))
* **frontend:** add min-width and max-width in price simulation cards ([310e9c0](https://github.com/FelipeBoos/PriceLab/commit/310e9c0ea2163056c8e59197ce425945a0304bc7))
* **frontend:** add price strategies page in sidebar navigation ([825cf8b](https://github.com/FelipeBoos/PriceLab/commit/825cf8b438961cc01385d1b3bf5873ffac59e10e))
* **frontend:** add price strategies route configuration ([7a416d6](https://github.com/FelipeBoos/PriceLab/commit/7a416d6d6e6282caec2c941faa12837406f65129))
* **frontend:** finish version 1 of frontend layout (closes [#4](https://github.com/FelipeBoos/PriceLab/issues/4)) ([439027a](https://github.com/FelipeBoos/PriceLab/commit/439027a5890827d340d52fbdc9746d31b47e4f41))
* **frontend:** update page name in adress bar and ad favicon ([acbdbf9](https://github.com/FelipeBoos/PriceLab/commit/acbdbf9da7b24b2faea3aac5ca051c4899a70ffd))
* **frontend:** update price strategies page and price strategy simulation page with new layout and update overall layout of other pages ([#4](https://github.com/FelipeBoos/PriceLab/issues/4)) ([402757f](https://github.com/FelipeBoos/PriceLab/commit/402757fe5e0e4bdee4f571e298c376245ab5d4b9))
* **layout:** implement initial dashboard layout with sidebar and page container ([6bedaf0](https://github.com/FelipeBoos/PriceLab/commit/6bedaf0b56f41361e2ffca67034e2952445f81cf))
* **login:** add initial structure for login page and route configuration ([9da53eb](https://github.com/FelipeBoos/PriceLab/commit/9da53eb258cdb886b67c2e2b16f3fa34c2c4b39d))
* **preco-controller:** add endpoint to create price strategy ([147b9b0](https://github.com/FelipeBoos/PriceLab/commit/147b9b0b932a123c94203512b1a7cb81756fea58))
* **preco-controller:** add endpoint to get price strategy by id ([c1025c1](https://github.com/FelipeBoos/PriceLab/commit/c1025c1e87106891c705c8c1ac5939d6c65efee2))
* **preco-controller:** add endpoint to list all price strategies ([163f9c6](https://github.com/FelipeBoos/PriceLab/commit/163f9c6890367491ab1fb92fc6c0eaa8c813ba52))
* **preco-controller:** add endpoint to list price strategies by produto id ([e88c3ad](https://github.com/FelipeBoos/PriceLab/commit/e88c3ad71a513a4a51e98a75d667a82c711002d1))
* **preco-controller:** add endpoint to simulate price strategy ([a1aa979](https://github.com/FelipeBoos/PriceLab/commit/a1aa9797bd9dd425eca266af8e8957d3d071d92d))
* **preco-db:** create estrategia_preco table ([5ef47b8](https://github.com/FelipeBoos/PriceLab/commit/5ef47b810a691354de6bf5ff5b6ffc0654d37032))
* **preco-dto:** create EstrategiaPrecoRequestDTO ([f0cdf5a](https://github.com/FelipeBoos/PriceLab/commit/f0cdf5a9f1db0d1b616b10d9b1ae6db8242881d9))
* **preco-dto:** create EstrategiaPrecoResponseDTO ([ec68ec3](https://github.com/FelipeBoos/PriceLab/commit/ec68ec3ead03b215b8a11733dec2fa96734b82a6))
* **preco-entity:** create EstrategiaPreco entity ([c0b6566](https://github.com/FelipeBoos/PriceLab/commit/c0b6566a434dd8cf4705342c5b49c4c64b374d6f))
* **preco-entity:** create EstrategiaPreco repository ([448b395](https://github.com/FelipeBoos/PriceLab/commit/448b3954c2a1142bf9c2fcb408b14a41be5d371c))
* **preco-service:** create EstrategiaPrecoService ([37fb11b](https://github.com/FelipeBoos/PriceLab/commit/37fb11b324b0378e6cfea51559d25d0e9a1d8f40))
* **product:** add endpoint to list all products ([41bab7f](https://github.com/FelipeBoos/PriceLab/commit/41bab7f3766ea310bc175739dc501f996406e3e6))
* **produto-db:** add pricing fields to produtos table ([008ed6a](https://github.com/FelipeBoos/PriceLab/commit/008ed6a51e64e4da062b181e2f3eb12add387c5c))
* **produto-dto:** include pricing fields in produto DTOs ([b3bac8a](https://github.com/FelipeBoos/PriceLab/commit/b3bac8a72b7736bb0034ce309f5f579da716c262))
* **produto-entity:** add pricing attributes to Produto entity ([e59e196](https://github.com/FelipeBoos/PriceLab/commit/e59e1968c8250a5b7174a4e88ed3adf82d39f431))
* **produto-service:** support pricing fields in produto creation and update ([36c2668](https://github.com/FelipeBoos/PriceLab/commit/36c266816a49fd54d2a3103f6ae7cd8280ae95b1))
* **produto:** implement product creation from frontend with backend integration ([1d56284](https://github.com/FelipeBoos/PriceLab/commit/1d56284c90e79357d631a1459266a7ed85546729))
* **produto:** implement product deletion from frontend ([7c6dced](https://github.com/FelipeBoos/PriceLab/commit/7c6dcedfa94892f2ea4c68dac5105e1fe2fafceb))
* **produto:** implement product listing in Angular frontend ([5aaac88](https://github.com/FelipeBoos/PriceLab/commit/5aaac884e127ef410d893ac539793ce70384ed00))
* **produto:** implement product update in frontend ([2f1fbf6](https://github.com/FelipeBoos/PriceLab/commit/2f1fbf65b6260dc14e1e9fb937ebda7f12a5e5cd))
* **router:** configure initial application routing with main layout and sidebar navigation ([0d0160e](https://github.com/FelipeBoos/PriceLab/commit/0d0160eba22558cb5ce4ff0dc5843bdb2742b529))
* **ui:** add reusable modal component ([3f13dc3](https://github.com/FelipeBoos/PriceLab/commit/3f13dc3308bc770877480a4109dc459fb5e8896f))


### Bug Fixes

* **categoria:** prevent deletion of categories with associated products ([6241212](https://github.com/FelipeBoos/PriceLab/commit/62412129014b3b523dd3b970d0928b6b6b8004a3))
* **layout:** remove default body margin causing white border in layout ([19157d1](https://github.com/FelipeBoos/PriceLab/commit/19157d10e9fca8a622677b7dfaf67a3404ac3cf4))
