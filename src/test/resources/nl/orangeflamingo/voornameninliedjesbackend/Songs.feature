Feature: Song controller

  Scenario: Check retrieval songs
    Given the artist 'Paul Simon'
    And the next song for artist 'Paul Simon':
      | title              | name | status |
      | You can call me Al | Al   | SHOW   |
    Then there are 1 songs returned