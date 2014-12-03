%% Event Classifier - Angathan FRANCIS

close all;
clear all;

[Event, Nonevent, Apple] = refsig(80, 1, 1);
[n, m] = size(Event);
dis = zeros(n, 1);
 gamma = 1;

for i = 1 : n,
    Eventdis(i) = distoref(Apple, Event(i, :));
end

for i = 1 : n,
    NonEventdis(i) = distoref(Apple, Nonevent(i, :));
end

EventProb = probclass(Eventdis, gamma);
NonEventProb = probclass(NonEventdis, gamma);
R = EventProb/NonEventProb

%plot(1 : 119, log10(Apple'), 'r', 1 : 238, log10(Event'))

% % beta1 = [1, 1.02, 1.04, 1.08, 1.1, 1.2, 1.3, 1.4, 1.5];
% % beta2 = [1, 0.9, 0.8, 0.7, 0.6, 0.5];
% % 
% % for j = 1 : 6,
% %     
% %     [Event, Nonevent, Apple] = refsig(60, beta1(j), beta2(j));
% %     [n, m] = size(Event);
% %     
% %     for i = 1 : n,
% %         Eventdis(i) = distoref(Apple, Event(i, :));
% %     end
% % 
% %     for i = 1 : n,
% %         NonEventdis(i) = distoref(Apple, Nonevent(i, :));
% %     end
% % 
% %     EventProb(j) = probclass(Eventdis, gamma);
% %     NonEventProb = probclass(NonEventdis, gamma);
% %     R(j) = EventProb(j)/NonEventProb;
% %     
% % end
    