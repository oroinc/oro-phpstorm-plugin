Changelog
=========

## 2025.2.0
- Updated compatibility with PHPStorm 2025.2
- Started reworking gradle build configuration

## 1.1.3
- Upgrade compatibility with 2025.1 version of PHPStorm.

## 1.1.2
- Upgrade compatibility with 2024.3 version of PHPStorm.
- Fix lack of support for "replace" keyword in action_groups node in actions.yml schema.

## 1.1.1
- Upgrade compatibility with 2024.2 version of PHPStorm.

## 1.1.0
- Fix problems with fetching RequireJS reference
- Restore ability to navigate to RequireJS references

## 2024.1
- Fixed reference provider errors.
- Updated dependencies.

## 2023.2
- Updated compatibility with PHPStorm 2023.2.3

## 2023.1
- Updated compatibility with PHPStorm 2023.1

## 2022.3
- Fixed compatibility with PHPStorm 2022.3

## 1.0.19
- [#17] Fixed IllegalArgumentException in Twig templates used by layouts in PHPStorm 2020.3

## 1.0.18
- [#15] extended_entity_name and table options for datagrids provide incorrect suggestions

## 1.0.17
- [#14] Fixed compatibility with the upcoming PhpStorm 2020.1

## 1.0.16
- [#12] Fix navigation to layout updates from layout themes (twig templates)

## 1.0.15
- Fix possible NullPointerException in requirejs support

## 1.0.14
- [#13] Fix possible deadlock and crash

## 1.0.13
- Better autocomplete for conditions in workflows.yml
- Support for translations autocomplete and navigation in more places
- Support for imported files in datagrids.yml

## 1.0.12
- Better autocomplete for actions / conditions in workflows.yml
- Fixed navigation to actions / conditions in workflows.yml

## 1.0.11
- Fixed support for scopes in workflows.yml
- Fixed two possible NullPointerExceptions

## 1.0.10
- Navigation and improved support for form types, api form types, actions, conditions and mass action providers

## 1.0.9
- Improved autocomplete for actions.yml

## 1.0.8
- Fix support for form_type in api.yml
- Fix support for autocomplete asset filepaths (eg. in assets.yml) starting with quote char
- Navigation to translations sources

## 1.0.7
- Autocomplete for scopes in workflows.yml

## 1.0.6
- Improved autocomplete for layout update yml files
- Navigation from twig templates to layout updates

## 1.0.5
- Fix completion for yaml files created from scratch

## 1.0.4
- A lot of improvements for autocomplete in various files, especially in: workflows.yml, system_configuration.yml, api.yml, actions.yml and navigation.yml

## 1.0.3
- Improved autocomplete for requirejs.yml files

## 1.0.2
- Improved autocomplete for layout update files
- Fix NullPointerException for autocomplete at the end of file

## 1.0.1
- Improved autocomplete for entity.yml
- Improved autocomplete for api.yml

## 1.0.0
- The same as 1.0.0-beta6 - only change versioning

## 1.0.0-beta6
- Improve performance by reducing number of reindexing.

## 1.0.0-beta5
- Full support for requirejs modules

## 1.0.0-beta4
- update autocomplete for dashboards.yml

## 1.0.0-beta3
- inspection for lack of value
- autocomplete for "label" properties in datagrids.yml
- improve autocomplete for "data_type" property in api.yml

## 1.0.0-beta2
- inspection for duplicate properties
- improve "data_transformer" suggestions in api.yml

## 1.0.0-beta1
- improve workflows.yml support
- inspections for value types

## 1.0.0-alpha23
- Small fixes for system_configuration.yml and api.yml

## 1.0.0-alpha22
- Support for PhpStorm 2016.3+

## 1.0.0-alpha21
- Autocomplete for more layouts yaml files: **theme.yml**, **assets.yml**, **requirejs.yml**, **images.yml**

## 1.0.0-alpha20
- Support for Oro Platform 2.+
- Autocomplete for layouts yaml files

## 1.0.0-alpha19
- Autocomplete for entity methods added dynamically during application execution

## 1.0.0-alpha18
- Autocomplete for the **search.yml**

## 1.0.0-alpha17
- Autocomplete for the **navigation.yml**

## 1.0.0-alpha16
- Autocomplete for the **dashboard.yml**

## 1.0.0-alpha15
- Autocomplete for the **actions.yml**

## 1.0.0-alpha14
- [#5] bug fix for reindex during php field rename refactoring
- Fixes for **api.yml** inspections

## 1.0.0-alpha13
- Autocomplete for the **api.yml**

## 1.0.0-alpha12
- Autocomplete for the **system_configuration.yml**
- Autocomplete for mixins in the **datagrid.yml**

## 1.0.0-alpha11
- Improvements for the plugin enabling / disabling feature
- Improvements for doctrine entity references support
- Improvements for Oro Platform detection logic

## 1.0.0-alpha10
- Autocomplete for the **acl.yml**
- Autocomplete for the **entity.yml**
- Autocomplete for the **datagrid.yml**
- Autocomplete for the **workflow.yml**
- Possibility to navigate via RequireJS modules
