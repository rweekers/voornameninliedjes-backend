Feature: Song controller

  Scenario: Check retrieval songs
    Given the artist 'The Beatles'
    And the next song for artist 'The Beatles':
      | title                         | name | status |
      | Lucy in the Sky with Diamonds | Lucy | SHOW   |
    And the next song for artist 'The Beatles':
      | title    | name     | status      |
      | Michelle | Michelle | IN_PROGRESS |
    Then there are 1 songs returned
    And there are 1 artists returned

  Scenario: Update song
    Given the artist 'The Beatles'
    And the next song for artist 'The Beatles':
      | title                         | name | status |
      | Lucy in the Sky with Diamonds | Lucy | SHOW   |
    When user Test updates the song 'Lucy in the Sky with Diamonds' with the following details:
      | title | name  | status      | artistName         |
      | Angie | Angie | IN_PROGRESS | The Rolling Stones |
    Then there are 1 artists with name 'The Rolling Stones'
    And there are 0 artists with name 'The Beatles'