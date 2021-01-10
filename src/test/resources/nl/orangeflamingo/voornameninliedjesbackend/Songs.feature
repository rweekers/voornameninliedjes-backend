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