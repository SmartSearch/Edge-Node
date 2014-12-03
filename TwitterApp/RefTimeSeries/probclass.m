%% Event Classifier - Angathan FRANCIS
% Provides the probability that the desired signal belongs to a specific label


function [proba] = probclass(distance, gamma)

    N = length(distance);
    proba = 0;
    
    for i = 1 : N,
        
        proba = proba + exp(-gamma * distance(i));
        
    end
    
end